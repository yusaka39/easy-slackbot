package io.github.yusaka39.easySlackbot.api.handler

import javax.swing.Action
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

interface HandlerProvider {
    fun createHandlerOrNull(
            annotations: List<Annotation>, kClass: KClass<*>, callable: KCallable<Action>
    ): Handler?
}