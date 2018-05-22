package com.github.yusaka39.slackbot.core.slack.impl

import com.github.yusaka39.slackbot.api.entity.Slack
import com.github.yusaka39.slackbot.core.slack.SlackFactory

class SimpleSlackApiSlackFactory : SlackFactory {
    override fun create(slackToken: String): Slack = SimpleSlackApiSlack(slackToken)
}