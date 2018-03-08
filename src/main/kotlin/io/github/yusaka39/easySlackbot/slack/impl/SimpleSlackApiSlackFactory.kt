package io.github.yusaka39.easySlackbot.slack.impl

import io.github.yusaka39.easySlackbot.slack.Slack
import io.github.yusaka39.easySlackbot.slack.SlackFactory

class SimpleSlackApiSlackFactory : SlackFactory {
    override fun create(slackToken: String): Slack = SimpleSlackApiSlack(slackToken)
}