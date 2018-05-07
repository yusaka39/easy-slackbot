package io.github.yusaka39.easySlackbot.router.actions

import io.github.yusaka39.easySlackbot.slack.Channel
import io.github.yusaka39.easySlackbot.slack.Slack

class PostAction(private val channelId: String, private val text: String) : Action {
    constructor(channel: Channel, text: String) : this(channel.id, text)

    override fun run(slack: Slack) {
        slack.sendTo(this.channelId, this.text)
    }
}