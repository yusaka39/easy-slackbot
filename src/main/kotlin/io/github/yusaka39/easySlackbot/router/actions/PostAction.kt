package io.github.yusaka39.easySlackbot.router.actions

import io.github.yusaka39.easySlackbot.api.entity.Action
import io.github.yusaka39.easySlackbot.api.entity.Channel
import io.github.yusaka39.easySlackbot.api.entity.Slack

class PostAction(private val channelId: String, private val text: String) : Action {
    constructor(channel: Channel, text: String) : this(channel.id, text)

    override fun run(slack: Slack) {
        slack.sendTo(this.channelId, this.text)
    }
}