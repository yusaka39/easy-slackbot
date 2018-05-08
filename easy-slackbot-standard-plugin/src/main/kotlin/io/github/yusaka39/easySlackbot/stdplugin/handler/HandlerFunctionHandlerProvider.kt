package io.github.yusaka39.easySlackbot.stdplugin.handler

import io.github.yusaka39.easySlackbot.api.handler.Handler
import io.github.yusaka39.easySlackbot.api.handler.HandlerProvider
import io.github.yusaka39.easySlackbot.stdplugin.handler.annotations.HandlerFunction
import javax.swing.Action
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

class HandlerFunctionHandlerProvider : HandlerProvider {
    override fun createHandlerOrNull(
            annotations: List<Annotation>, kClass: KClass<*>, callable: KCallable<Action>
    ): Handler? {
        val annotation =
                annotations.firstOrNull { it is HandlerFunction } as? HandlerFunction ?: return null
        return HandlerFunctionHandler(
                kClass, callable,
                annotation.regex.toRegex(annotation.regexOption.toSet()), annotation.type.toList()
        )
    }
}