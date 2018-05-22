package com.github.yusaka39.slackbot.stdplugin.actions

import com.github.yusaka39.slackbot.api.entity.Action
import com.github.yusaka39.slackbot.api.entity.Message
import com.github.yusaka39.slackbot.api.entity.Slack

class PutReactionAction(
        private val channelId: String,
        private val timestamp: String,
        private val emoticonName: String
) : Action {
    constructor(message: Message, emoticonName: String) : this(message.channel.id, message.timestamp, emoticonName)

    override fun run(slack: Slack) {
        slack.putReactionTo(this.channelId, this.timestamp, this.emoticonName)
    }
}