package io.github.yusaka39.easySlackbot.slack.impl

import io.github.yusaka39.easySlackbot.slack.*

class SlackletSlack(slackToken: String) : Slack {

    override fun sendTo(channel: Channel, text: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun putAttachmentTo(channel: Channel, attachment: Attachment) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun putReactionTo(message: Message, emoticonName: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendDirectMessageTo(user: User, text: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onReceiveMessage(handler: (message: Message, slack: Slack) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onReceiveDirectMessage(handler: (message: Message, slack: Slack) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onReceiveRepliedMessage(handler: (message: Message, slack: Slack) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getChannelIdByName(channelName: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startService() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stopService() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}