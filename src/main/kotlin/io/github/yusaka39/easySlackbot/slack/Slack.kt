package io.github.yusaka39.easySlackbot.slack

interface Slack {
    fun sendTo(channelId: String, text: String)
    fun putAttachmentTo(channelId: String, attachment: Attachment)
    fun putAttachmentTo(user: User, attachment: Attachment)
    fun putReactionTo(channelId: String, timestamp: String, emoticonName: String)
    fun sendDirectMessageTo(user: User, text: String)
    fun onReceiveMessage(handler: (message: Message, slack: Slack) -> Unit)
    fun onReceiveDirectMessage(handler: (message: Message, slack: Slack) -> Unit)
    fun onReceiveRepliedMessage(handler: (message: Message, slack: Slack) -> Unit)
    fun getChannelOrNullByName(channelName: String): Channel?
    fun startService()
    fun stopService()
}