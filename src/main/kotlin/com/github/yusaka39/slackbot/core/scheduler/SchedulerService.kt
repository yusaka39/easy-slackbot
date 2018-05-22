package com.github.yusaka39.slackbot.core.scheduler

import com.github.yusaka39.slackbot.api.entity.Slack

internal interface SchedulerService {
    fun start(slack: Slack)
    fun stop()
}