package com.github.yusaka39.slackbot.core.scheduler

internal class AnnotationBasedSchedulerServiceFactory(private val packageName: String) : SchedulerServiceFactory {
    override fun create(): SchedulerServiceImpl =
            SchedulerServiceImpl(AnnotationBasedScheduledTaskSetFactory(this.packageName))
}