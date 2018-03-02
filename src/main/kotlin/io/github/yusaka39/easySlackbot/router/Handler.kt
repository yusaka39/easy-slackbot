package io.github.yusaka39.easySlackbot.router

import io.github.yusaka39.easySlackbot.annotations.GroupParam
import io.github.yusaka39.easySlackbot.lib.convertTo
import io.github.yusaka39.easySlackbot.router.actions.Action
import io.github.yusaka39.easySlackbot.slack.Message
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters


class Handler(private val kClass: KClass<*>, private val kCallable: KCallable<Action>,
              private val regex: Regex, private val handlerType: HandlerType) {
    enum class HandlerType {
        RespondTo, ListenTo
    }
    fun isMatchTo(message: Message, type: HandlerType): Boolean =
            this.handlerType == type && regex.matches(message.text)

    fun generateActionForMessage(message: Message): Action = this.kCallable.call(
            instantiateWithPrimaryConstructor(this.kClass),
            *this.getArgumentsFromTargetMessage(message).toTypedArray()
    )

    private fun instantiateWithPrimaryConstructor(kClass: KClass<*>): Any = try {
        this.kClass.primaryConstructor?.call() ?: throw IllegalStateException(
                "Classes contains handler function must have primary constructor"
        )
    } catch (e: IllegalArgumentException) {
        throw IllegalStateException(
                "Classes contains handler function must have primary constructor without any arguments"
        )
    }

    private fun getArgumentsFromTargetMessage(message: Message): List<Any?> {
        val params = this.kCallable.valueParameters.map {
            it to (it.findAnnotation<GroupParam>()?.group ?: throw IllegalStateException(
                    "All params of methods annotated by ListenTo or RespondTo must be annotated by GroupParam."))
        }
        val group = this.regex.find(message.text)?.groupValues!!
        return params.map { (param, index) -> group[index].convertTo(param.type) }
    }
}

/*
private enum class ResponseType {
    ListenTo, RespondTo;

    companion object {
        fun from(annotation: Annotation): ResponseType? = when (annotation) {
            is io.github.yusaka39.easySlackbot.annotations.ListenTo -> ListenTo
            is io.github.yusaka39.easySlackbot.annotations.RespondTo -> RespondTo
            else -> null
        }
    }

    fun getRegexFromAnnotation(annotation: Annotation): Regex = when (this) {
            ResponseType.ListenTo -> (annotation as? io.github.yusaka39.easySlackbot.annotations.ListenTo)?.regexp
            ResponseType.RespondTo -> (annotation as? io.github.yusaka39.easySlackbot.annotations.RespondTo)?.regexp
    }?.toRegex() ?: throw IllegalStateException("Given annotation is not a '${this.name}'")
}
 */