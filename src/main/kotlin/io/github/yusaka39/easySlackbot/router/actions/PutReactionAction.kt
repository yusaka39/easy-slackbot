package io.github.yusaka39.easySlackbot.router.actions

import io.github.yusaka39.easySlackbot.slack.Message
import io.github.yusaka39.easySlackbot.slack.Slack

class PutReactionAction(private val channelId: String,
                        private val timestamp: Long,
                        private val emoticonName: String) : Action {
    constructor(message: Message, emoticonName: String) : this(message.channel.id, message.timestamp, emoticonName)

    override fun run(slack: Slack) {
        slack.putReactionTo(this.channelId, this.timestamp, this.emoticonName)
    }
}