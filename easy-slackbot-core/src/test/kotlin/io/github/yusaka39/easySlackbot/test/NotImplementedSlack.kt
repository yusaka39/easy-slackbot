package io.github.yusaka39.easySlackbot.test

import io.github.yusaka39.easySlackbot.api.entity.Attachment
import io.github.yusaka39.easySlackbot.api.entity.Message
import io.github.yusaka39.easySlackbot.slack.Slack

open class NotImplementedSlack : Slack {
    override fun sendTo(channelId: String, text: String) {
        TODO("not implemented")
    }

    override fun putAttachmentTo(channelId: String, vararg attachments: Attachment) {
        TODO("not implemented")
    }

    override fun putReactionTo(channelId: String, timestamp: String, emoticonName: String) {
        TODO("not implemented")
    }

    override fun sendDirectMessageTo(username: String, text: String) {
        TODO("not implemented")
    }

    override fun onReceiveMessage(handler: (message: Message, slack: Slack) -> Unit) {
        TODO("not implemented")
    }

    override fun onReceiveDirectMessage(handler: (message: Message, slack: Slack) -> Unit) {
        TODO("not implemented")
    }

    override fun onReceiveReply(handler: (message: Message, slack: Slack) -> Unit) {
        TODO("not implemented")
    }

    override fun getChannelIdOrNullByName(channelName: String): String? {
        TODO("not implemented")
    }

    override fun getUserIdByName(userName: String): String? {
        TODO("not implemented")
    }

    override fun getDmChannelIdOrNullByUserName(username: String): String? {
        TODO("not implemented")
    }

    override fun startService() {
        TODO("not implemented")
    }

    override fun stopService() {
        TODO("not implemented")
    }
}