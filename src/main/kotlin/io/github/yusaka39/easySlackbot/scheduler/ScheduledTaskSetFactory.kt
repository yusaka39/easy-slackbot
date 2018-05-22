package io.github.yusaka39.easySlackbot.scheduler

internal interface ScheduledTaskSetFactory {
    fun create(): Set<ScheduledTask>
}