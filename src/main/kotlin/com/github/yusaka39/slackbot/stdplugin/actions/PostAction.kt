package com.github.yusaka39.slackbot.stdplugin.actions

import com.github.yusaka39.slackbot.api.entity.Action
import com.github.yusaka39.slackbot.api.entity.Channel
import com.github.yusaka39.slackbot.api.entity.Slack

class PostAction(private val channelId: String, private val text: String) : Action {
    constructor(channel: Channel, text: String) : this(channel.id, text)

    override fun run(slack: Slack) {
        slack.sendTo(this.channelId, this.text)
    }
}