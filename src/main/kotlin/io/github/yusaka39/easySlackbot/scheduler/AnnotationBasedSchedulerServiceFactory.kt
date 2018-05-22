package io.github.yusaka39.easySlackbot.scheduler

internal class AnnotationBasedSchedulerServiceFactory(private val packageName: String) : SchedulerServiceFactory {
    override fun create(): SchedulerServiceImpl =
            SchedulerServiceImpl(AnnotationBasedScheduledTaskSetFactory(this.packageName))
}