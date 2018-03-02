package io.github.yusaka39.easySlackbot.router.actions

import io.github.yusaka39.easySlackbot.slack.Channel
import io.github.yusaka39.easySlackbot.slack.Slack

class PostAction(private val channel: Channel, private val text: String) : Action {
    override fun run(slack: Slack) {
        slack.sendTo(this.channel.id, this.text)
    }
}