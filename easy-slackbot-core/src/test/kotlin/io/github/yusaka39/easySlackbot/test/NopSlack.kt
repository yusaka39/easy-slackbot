package io.github.yusaka39.easySlackbot.test

import io.github.yusaka39.easySlackbot.api.entity.Attachment
import io.github.yusaka39.easySlackbot.api.entity.Message
import io.github.yusaka39.easySlackbot.api.entity.Slack

open class NopSlack : Slack {
    override fun sendTo(channelId: String, text: String) {}
    override fun putAttachmentTo(channelId: String, vararg attachments: Attachment) {}
    override fun putReactionTo(channelId: String, timestamp: String, emoticonName: String) {}
    override fun sendDirectMessageTo(username: String, text: String) {}
    override fun onReceiveMessage(handler: (message: Message, slack: Slack) -> Unit) {}
    override fun onReceiveDirectMessage(handler: (message: Message, slack: Slack) -> Unit) {}
    override fun onReceiveReply(handler: (message: Message, slack: Slack) -> Unit) {}
    override fun getChannelIdOrNullByName(channelName: String): String? = null
    override fun getUserIdByName(userName: String): String? = null
    override fun getDmChannelIdOrNullByUserName(username: String): String? = null
    override fun startService() {}
    override fun stopService() {}
}