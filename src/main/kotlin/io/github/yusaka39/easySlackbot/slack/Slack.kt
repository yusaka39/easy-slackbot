package io.github.yusaka39.easySlackbot.slack

import javax.jws.soap.SOAPBinding

interface Slack {
    fun sendTo(channel: Channel, text: String)
    fun putAttachmentTo(channel: Channel, attachment: Attachment)
    fun putReactionTo(message: Message, emoticonName: String)
    fun sendDirectMessageTo(user: User, text: String)
    fun onReceiveMessage(handler: (message: Message) -> Unit, slack: Slack)
    fun onReceiveDirectMessage(handler: (message: Message) -> Unit, slack: Slack)
    fun onReceiveRepliedMessage(handler: (message: Message) -> Unit, slack: Slack)
}