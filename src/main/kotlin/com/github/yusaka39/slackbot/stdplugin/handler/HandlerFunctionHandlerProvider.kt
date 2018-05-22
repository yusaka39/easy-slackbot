package com.github.yusaka39.slackbot.stdplugin.handler

import com.github.yusaka39.slackbot.api.entity.Action
import com.github.yusaka39.slackbot.api.handler.Handler
import com.github.yusaka39.slackbot.api.handler.HandlerProvider
import com.github.yusaka39.slackbot.stdplugin.handler.annotations.HandlerFunction
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