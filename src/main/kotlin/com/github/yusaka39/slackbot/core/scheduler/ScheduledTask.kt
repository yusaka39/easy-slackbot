package com.github.yusaka39.slackbot.core.scheduler

import com.github.yusaka39.slackbot.api.entity.Action
import com.github.yusaka39.slackbot.api.entity.Message
import com.github.yusaka39.slackbot.api.handler.HandlerPack
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.isAccessible

internal class ScheduledTask(
        private val kClass: KClass<*>,
        private val kCallable: KCallable<*>,
        val schedule: Schedule
) {
    fun getAction(): Action = (this.kCallable.apply {
        this.isAccessible = true
    }.call(this.kClass.instantiateHandlerPack()) as? Action) ?: throw IllegalStateException(
            "Functions annotated with RunWithInterval must return Action"
    )

    override fun toString(): String = "ScheduledTask[ startedAt: %02d:%02d, function: $kCallable ]"
            .format(this.schedule.startHour, this.schedule.startMin)

    // TODO: duplicate definition
    private fun KClass<*>.instantiateHandlerPack(message: Message? = null): Any {
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
}