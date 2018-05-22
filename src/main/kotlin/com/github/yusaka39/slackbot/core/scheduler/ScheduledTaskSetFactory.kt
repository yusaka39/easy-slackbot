package com.github.yusaka39.slackbot.core.scheduler

internal interface ScheduledTaskSetFactory {
    fun create(): Set<ScheduledTask>
}