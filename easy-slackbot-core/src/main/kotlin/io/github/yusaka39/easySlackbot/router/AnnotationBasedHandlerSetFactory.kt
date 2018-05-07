package io.github.yusaka39.easySlackbot.router

import com.google.common.reflect.ClassPath
import io.github.yusaka39.easySlackbot.annotations.HandlerFunction
import io.github.yusaka39.easySlackbot.lib.logger
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

internal class AnnotationBasedHandlerSetFactory(private val packageName: String) : HandlerSetFactory {
    private val logger by this.logger()
    @Suppress("unchecked_cast")
    override fun create(): Set<Handler> =
            ClassPath.from(ClassLoader.getSystemClassLoader()).allClasses.filter {
                it.packageName.startsWith(this.packageName)
            }.flatMap {
                try {
                    val kClass = it.load().kotlin
                    (kClass.members + kClass.memberProperties.map { it.getter }).flatMap inner@{ kCallable ->
                        val annotation = kCallable.findAnnotation<HandlerFunction>()
                                ?: return@inner emptyList<Handler>()
                        annotation.type.map {
                            Handler(kClass, kCallable, annotation.regex.toRegex(annotation.regexOption.toSet()), it)
                        }
                    }
                } catch (e: UnsupportedOperationException) {
                    this.logger.warn("Failed to parse class ${it.name}.", e)
                    emptyList<Handler>()
                }
            }.toSet()
}
