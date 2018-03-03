package io.github.yusaka39.easySlackbot.router

import io.github.yusaka39.easySlackbot.annotations.GroupParam
import io.github.yusaka39.easySlackbot.lib.convertTo
import io.github.yusaka39.easySlackbot.router.actions.Action
import io.github.yusaka39.easySlackbot.slack.Message
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
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
        instantiateWithPrimaryConstructor(this.kClass, message),
        *this.getArgumentsFromTargetMessage(message).toTypedArray()
    ) as? Action ?: throw IllegalStateException("Handler functions must return Action")

    private fun instantiateWithPrimaryConstructor(kClass: KClass<*>, message: Message): Any = try {
        if (!kClass.isSubclassOf(HandlerPack::class)) {
            throw IllegalStateException("Classes contains handler must extends HandlerPack")
        }
        kClass.primaryConstructor?.apply {
            this.isAccessible = true
        }?.call()?.apply {
            (this as HandlerPack)._receivedMessage = message
        } ?: throw IllegalStateException(
            "Classes contains handler function must have primary constructor"
        )
    } catch (e: IllegalArgumentException) {
        throw IllegalStateException(
            "Classes contains handler function must have primary constructor without any arguments", e
        )
    }

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
