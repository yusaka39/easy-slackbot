package com.github.yusaka39.slackbot.core.slack

import com.github.yusaka39.slackbot.api.entity.Slack

interface SlackFactory {
    fun create(slackToken: String): Slack
}