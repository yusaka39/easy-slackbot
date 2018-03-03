package io.github.yusaka39.easySlackbot.router

import com.google.common.reflect.ClassPath
import io.github.yusaka39.easySlackbot.annotations.HandlerFunction
import io.github.yusaka39.easySlackbot.lib.Log
import kotlin.reflect.full.findAnnotation

class AnnotationBasedHandlerSetFactory(private val packageName: String) : HandlerSetFactory {
    @Suppress("unchecked_cast")
    override fun create(): Set<Handler> =
        ClassPath.from(ClassLoader.getSystemClassLoader()).getTopLevelClassesRecursive(this.packageName).flatMap {
            try {
                val kClass = it.load().kotlin
                kClass.members.flatMap inner@{ kCallable ->
                    val annotation = kCallable.findAnnotation<HandlerFunction>()
                            ?: return@inner emptyList<Handler>()
                    annotation.type.map {
                        Handler(kClass, kCallable, annotation.regex.toRegex(annotation.regexOption.toSet()), it)
                    }
                }
            } catch (e: UnsupportedOperationException) {
                Log.w("Failed to parse class ${it.name}.", e)
                emptyList<Handler>()
            }
        }.toSet()
}
