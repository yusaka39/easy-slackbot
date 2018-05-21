package io.github.yusaka39.easySlackbot.api.handler

import io.github.yusaka39.easySlackbot.api.entity.Action
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

interface HandlerProvider {
    fun createHandlerOrNull(
            annotations: List<Annotation>, kClass: KClass<*>, callable: KCallable<Action>
    ): Handler?
}