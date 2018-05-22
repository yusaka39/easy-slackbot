package com.github.yusaka39.slackbot.core.scheduler

internal interface SchedulerServiceFactory {
    fun create(): SchedulerService
}