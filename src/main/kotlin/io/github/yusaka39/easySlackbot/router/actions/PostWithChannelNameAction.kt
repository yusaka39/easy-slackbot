package io.github.yusaka39.easySlackbot.router.actions

import io.github.yusaka39.easySlackbot.slack.Slack

class PostWithChannelNameAction(private val channelName: String, private val text: String) : Action {
    override fun run(slack: Slack) {
        val channelId = slack.getChannelIdOrNullByName(this.channelName) ?: throw IllegalStateException(
            "No channel named ${this.channelName} found"
        )
        slack.sendTo(channelId, this.text)
    }
}