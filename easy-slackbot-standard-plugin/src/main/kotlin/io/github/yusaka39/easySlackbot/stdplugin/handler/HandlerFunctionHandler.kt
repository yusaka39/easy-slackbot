package io.github.yusaka39.easySlackbot.stdplugin.handler

import io.github.yusaka39.easySlackbot.api.entity.Action
import io.github.yusaka39.easySlackbot.api.entity.Message
import io.github.yusaka39.easySlackbot.api.handler.Handler
import io.github.yusaka39.easySlackbot.api.handler.HandlerPack
import io.github.yusaka39.easySlackbot.stdplugin.handler.annotations.GroupParam
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.isAccessible

enum class HandlerType {
    RespondTo, ListenTo
}

class HandlerFunctionHandler(
        private val kClass: KClass<*>,
        private val kCallable: KCallable<*>,
        private val regex: Regex,
        private val handlerTypes: List<HandlerType>
) : Handler {
    override fun createActionProvider(message: Message): () -> Action = {
        this.kCallable.apply {
            this.isAccessible = true
        }.call(
                this.kClass.instantiateHandlerPack(message),
                *this.getArgumentsFromTargetMessage(message).toTypedArray()
        ) as? Action ?: throw IllegalStateException("Handler functions must return Action")
    }

    override fun isMatchedTo(message: Message, isReplyMessage: Boolean): Boolean {
        if (handlerTypes.isEmpty()) {
            return false
        }
        if (HandlerType.ListenTo !in handlerTypes && !isReplyMessage) {
            return false
        }
        return this.regex.find(message.text) != null
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
}

internal fun KClass<*>.instantiateHandlerPack(message: Message? = null): Any {
    if (!this.isSubclassOf(HandlerPack::class)) {
        throw IllegalStateException("Classes contains handler must extends HandlerPack")
    }
    return this.constructors.firstOrNull { it.parameters.isEmpty() }?.apply {
        this.isAccessible = true
    }?.call()?.apply {
        (this as HandlerPack).setReceivedMessage(message)
    } ?: throw IllegalStateException(
            "Classes contains handler function must have constructor with no args"
    )
}

internal fun String.convertTo(kType: KType): Any? {
    val isNullable = kType.isMarkedNullable
    return when (kType.classifier) {
        Byte::class -> this.toByteOrNull().validateNullability(isNullable)
        Short::class -> this.toShortOrNull().validateNullability(isNullable)
        Int::class -> this.toIntOrNull().validateNullability(isNullable)
        Long::class -> this.toLongOrNull().validateNullability(isNullable)
        Float::class -> this.toFloatOrNull().validateNullability(isNullable)
        Double::class -> this.toDoubleOrNull().validateNullability(isNullable)
        Boolean::class -> when (this) {
            "true" -> true
            "false" -> false
            else -> null
        }.validateNullability(isNullable)
        String::class -> this
        else -> throw IllegalArgumentException("${kType.classifier} is not allowed as parameter")
    }
}

private inline fun <reified T> T?.validateNullability(isNullable: Boolean): T? {
    if (this != null) {
        return this
    }
    if (isNullable) {
        return null
    }
    throw IllegalArgumentException("$this cannot convert to ${T::class.simpleName}.")
}

