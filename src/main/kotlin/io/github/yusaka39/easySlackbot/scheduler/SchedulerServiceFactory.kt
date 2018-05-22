package io.github.yusaka39.easySlackbot.scheduler

internal interface SchedulerServiceFactory {
    fun create(): SchedulerService
}