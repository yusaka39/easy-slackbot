package io.github.yusaka39.easySlackbot.router

import io.github.yusaka39.easySlackbot.annotations.GroupParam
import io.github.yusaka39.easySlackbot.api.entity.Action
import io.github.yusaka39.easySlackbot.api.entity.Message
import io.github.yusaka39.easySlackbot.lib.convertTo
import io.github.yusaka39.easySlackbot.lib.instantiateHandlerPack
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.isAccessible

enum class HandlerType {
    RespondTo, ListenTo
}

internal class Handler(
        private val kClass: KClass<*>, private val kCallable: KCallable<*>,
        private val regex: Regex, private val handlerType: HandlerType
) {

    fun isMatchTo(message: Message, type: HandlerType): Boolean =
            this.handlerType == type && regex.find(message.text) != null

    fun generateActionForMessage(message: Message): Action = this.kCallable.apply {
        this.isAccessible = true
    }.call(
            kClass.instantiateHandlerPack(message),
            *this.getArgumentsFromTargetMessage(message).toTypedArray()
    ) as? Action ?: throw IllegalStateException("Handler functions must return Action")



    override fun toString(): String = "Handler[regex: $regex, function: $kCallable]"
}
