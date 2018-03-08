package io.github.yusaka39.easySlackbot.scheduler

import io.github.yusaka39.easySlackbot.slack.Slack

internal interface SchedulerService {
    fun start(slack: Slack)
    fun stop()
}