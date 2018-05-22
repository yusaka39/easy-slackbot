package com.github.yusaka39.slackbot.stdplugin.handler

import com.github.yusaka39.slackbot.api.entity.Action
import com.github.yusaka39.slackbot.api.entity.Message
import com.github.yusaka39.slackbot.api.handler.Handler
import com.github.yusaka39.slackbot.api.handler.HandlerPack
import com.github.yusaka39.slackbot.api.handler.HandlerPackHandler
import com.github.yusaka39.slackbot.stdplugin.handler.annotations.GroupParam
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
        override val kClass: KClass<*>,
        override val kCallable: KCallable<*>,
        private val regex: Regex,
        private val handlerTypes: List<HandlerType>
) : HandlerPackHandler() {
    override fun isMatchedTo(message: Message, isReplyMessage: Boolean): Boolean {
        if (handlerTypes.isEmpty()) {
            return false
        }
        if (HandlerType.ListenTo !in handlerTypes && !isReplyMessage) {
            return false
        }
        return this.regex.find(message.text) != null
    }

    override fun getArgumentsFromTargetMessage(message: Message): Array<Any?> {
        val params = this.kCallable.valueParameters.map {
            it to (it.findAnnotation<GroupParam>()?.group ?: throw IllegalStateException(
                    "All params of methods annotated by ListenTo or RespondTo must be annotated by GroupParam."
            ))
        }
        val group = this.regex.find(message.text)?.groupValues ?: throw IllegalStateException(
                "Given message $message is not match to $this"
        )
        return params.map { (param, index) -> group[index].convertTo(param.type) }.toTypedArray()
    }

    private fun String.convertTo(kType: KType): Any? {
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
}



