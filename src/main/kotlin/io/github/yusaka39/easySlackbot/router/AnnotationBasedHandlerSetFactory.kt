package io.github.yusaka39.easySlackbot.router

import com.google.common.reflect.ClassPath
import io.github.yusaka39.easySlackbot.annotations.ListenTo
import io.github.yusaka39.easySlackbot.annotations.RespondTo
import io.github.yusaka39.easySlackbot.lib.isAnnotatedWith
import io.github.yusaka39.easySlackbot.router.actions.Action
import kotlin.reflect.KCallable
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
                    val callable = it as? KCallable<Action> ?: throw IllegalStateException(
                            "Handler functions must return Action"
                    )
                    if (callable.isAnnotatedWith<ListenTo>()) {
                        val annotation = callable.findAnnotation<ListenTo>()!!
                        handlers += Handler(kClass, callable, annotation.regexp.toRegex(), Handler.HandlerType.ListenTo)
                    }
                    if (callable.isAnnotatedWith<RespondTo>()) {
                        val annotation = callable.findAnnotation<RespondTo>()!!
                        handlers +=
                                Handler(kClass, callable, annotation.regexp.toRegex(), Handler.HandlerType.RespondTo)
                    }
                }
                handlers
            }.toSet()
}
