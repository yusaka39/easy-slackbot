package io.github.yusaka39.easySlackbot.slack

import io.github.yusaka39.easySlackbot.api.entity.Slack

interface SlackFactory {
    fun create(slackToken: String): Slack
}