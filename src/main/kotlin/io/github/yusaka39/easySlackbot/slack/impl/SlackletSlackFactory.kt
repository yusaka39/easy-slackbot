package io.github.yusaka39.easySlackbot.slack.impl

import io.github.yusaka39.easySlackbot.slack.Slack
import io.github.yusaka39.easySlackbot.slack.SlackFactory

internal class SlackletSlackFactory : SlackFactory {
    override fun create(slackToken: String): Slack = SlackletSlack(slackToken)
}