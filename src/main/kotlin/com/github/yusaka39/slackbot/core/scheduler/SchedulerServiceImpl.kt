package com.github.yusaka39.slackbot.core.scheduler

import com.github.yusaka39.slackbot.api.entity.Slack
import com.github.yusaka39.slackbot.core.lib.logger
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