package io.github.yusaka39.easySlackbot.router

import com.google.common.reflect.ClassPath
import io.github.yusaka39.easySlackbot.annotations.ListenTo
import io.github.yusaka39.easySlackbot.annotations.RespondTo
import io.github.yusaka39.easySlackbot.lib.isAnnotatedWith
import kotlin.reflect.full.findAnnotation

class AnnotationBasedHandlerSetFactory(private val packageName: String) : HandlerSetFactory {
    @Suppress("unchecked_cast")
    override fun create(): Set<Handler> =
            ClassPath.from(ClassLoader.getSystemClassLoader()).getTopLevelClassesRecursive(this.packageName).flatMap {
                val kClass = it.load().kotlin
                val handlers = mutableListOf<Handler>()
                kClass.members.filter {
                    it.isAnnotatedWith<ListenTo>() or it.isAnnotatedWith<RespondTo>()
                }.forEach {
                    if (it.isAnnotatedWith<ListenTo>()) {
                        val annotation = it.findAnnotation<ListenTo>()!!
                        handlers += Handler(kClass, it, annotation.regexp.toRegex(), Handler.HandlerType.ListenTo)
                    }
                    if (it.isAnnotatedWith<RespondTo>()) {
                        val annotation = it.findAnnotation<RespondTo>()!!
                        handlers +=
                                Handler(kClass, it, annotation.regexp.toRegex(), Handler.HandlerType.RespondTo)
                    }
                }
                handlers
            }.toSet()
}
