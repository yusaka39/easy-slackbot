package io.github.yusaka39.easySlackbot.scheduler

import io.github.yusaka39.easySlackbot.lib.logger
import io.github.yusaka39.easySlackbot.slack.Slack
import java.util.Timer
import kotlin.concurrent.timer

internal class SchedulerServiceImpl(scheduledTaskSetFactory: ScheduledTaskSetFactory) : SchedulerService {
    private val logger by this.logger()
    private val tasks = scheduledTaskSetFactory.create()
    private var runningTask: List<Timer> = emptyList()

    override fun start(slack: Slack) {
        this.logger.info("Starting scheduled service.")
        this.runningTask = this.tasks.map {
            timer(
                initialDelay = it.schedule.getDelayToFirstExecution(),
                period = it.schedule.intervalMillis
            ) {
                this@SchedulerServiceImpl.logger.info("Calling $it.")
                it.getAction().run(slack)
            }
        }
    }

    override fun stop() {
        this.runningTask.forEach {
            this.logger.info("Stopping scheduled service.")
            it.cancel()
        }
    }
}