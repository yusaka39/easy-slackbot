package io.github.yusaka39.easySlackbot.scheduler

import io.github.yusaka39.easySlackbot.api.entity.Action
import io.github.yusaka39.easySlackbot.lib.instantiateHandlerPack
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
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
}