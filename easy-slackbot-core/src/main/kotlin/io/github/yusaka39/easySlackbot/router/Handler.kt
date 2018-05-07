package io.github.yusaka39.easySlackbot.router

import io.github.yusaka39.easySlackbot.annotations.GroupParam
import io.github.yusaka39.easySlackbot.api.entity.Message
import io.github.yusaka39.easySlackbot.lib.convertTo
import io.github.yusaka39.easySlackbot.lib.instantiateHandlerPack
import io.github.yusaka39.easySlackbot.api.entity.Action
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

    private fun getArgumentsFromTargetMessage(message: Message): List<Any?> {
        val params = this.kCallable.valueParameters.map {
            it to (it.findAnnotation<GroupParam>()?.group ?: throw IllegalStateException(
                "All params of methods annotated by ListenTo or RespondTo must be annotated by GroupParam."
            ))
        }
        val group = this.regex.find(message.text)?.groupValues ?: throw IllegalStateException(
            "Given message $message is not match to $this"
        )
        return params.map { (param, index) -> group[index].convertTo(param.type) }
    }

    override fun toString(): String = "Handler[regex: $regex, function: $kCallable]"
}
