package io.github.yusaka39.easySlackbot.slack

interface SlackFactory {
    fun create(slackToken: String): Slack
}