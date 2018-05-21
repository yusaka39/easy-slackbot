package io.github.yusaka39.easySlackbot.router

import com.google.common.reflect.ClassPath
import io.github.yusaka39.easySlackbot.api.entity.Action
import io.github.yusaka39.easySlackbot.api.handler.Handler
import io.github.yusaka39.easySlackbot.api.handler.HandlerProvider
import io.github.yusaka39.easySlackbot.lib.logger
import kotlin.reflect.KCallable
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties

class HandlerSetFactoryImpl(
        private val searchPackage: String,
        private val handlerProviders: List<HandlerProvider>
): HandlerSetFactory {
    private val logger by this.logger()

    @Suppress("unchecked_cast", "name_shadowing")
    override fun create(): Set<Handler> {
        return ClassPath.from(ClassLoader.getSystemClassLoader()).allClasses.filter {
            it.packageName.startsWith(this.searchPackage)
        }.flatMap {
            try {
                val kClass = it.load().kotlin
                (kClass.members + kClass.memberProperties.map { it.getter }).filter {
                    it.returnType.isSubtypeOf(Action::class.createType())
                }.flatMap {
                    val kCallable = it as KCallable<Action>
                    val annotations = kCallable.annotations
                    this.handlerProviders.mapNotNull {
                        it.createHandlerOrNull(annotations, kClass, kCallable)
                    }
                }
            } catch (e: UnsupportedOperationException) {
                this.logger.warn("Failed to parse class ${it.name}.", e)
                emptyList<Handler>()
            }
        }.toSet()
    }
}