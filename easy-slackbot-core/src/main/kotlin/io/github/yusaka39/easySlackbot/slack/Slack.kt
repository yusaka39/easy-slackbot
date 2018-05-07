package io.github.yusaka39.easySlackbot.slack

import io.github.yusaka39.easySlackbot.api.entity.Attachment
import io.github.yusaka39.easySlackbot.api.entity.Message

interface Slack {
    fun sendTo(channelId: String, text: String)
    fun putAttachmentTo(channelId: String, vararg attachments: Attachment)
    fun putReactionTo(channelId: String, timestamp: String, emoticonName: String)
    fun sendDirectMessageTo(username: String, text: String)
    fun onReceiveMessage(handler: (message: Message, slack: Slack) -> Unit)
    fun onReceiveDirectMessage(handler: (message: Message, slack: Slack) -> Unit)
    fun onReceiveReply(handler: (message: Message, slack: Slack) -> Unit)
    fun getChannelIdOrNullByName(channelName: String): String?
    fun getUserIdByName(userName: String): String?
    fun getDmChannelIdOrNullByUserName(username: String): String?
    fun startService()
    fun stopService()
}