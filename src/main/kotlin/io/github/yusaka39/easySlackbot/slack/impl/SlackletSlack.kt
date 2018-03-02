package io.github.yusaka39.easySlackbot.slack.impl

import io.github.yusaka39.easySlackbot.lib.getMessage
import io.github.yusaka39.easySlackbot.slack.*
import org.riversun.slacklet.Slacklet
import org.riversun.slacklet.SlackletRequest
import org.riversun.slacklet.SlackletResponse
import org.riversun.slacklet.SlackletService
import org.riversun.xternal.simpleslackapi.SlackChannel

private typealias ChannelId = String

class SlackletSlack(slackToken: String) : Slack {
    private val service = SlackletService(slackToken).apply {
        this.addSlacklet(object : Slacklet() {
            override fun onMentionedMessagePosted(req: SlackletRequest, resp: SlackletResponse) {
                this@SlackletSlack.onReceiveRepliedMessage(req.getMessage(), this@SlackletSlack)
            }

            override fun onMessagePosted(req: SlackletRequest, resp: SlackletResponse) {
                this@SlackletSlack.onReceiveMessage(req.getMessage(), this@SlackletSlack)
            }

            override fun onDirectMessagePosted(req: SlackletRequest, resp: SlackletResponse) {
                this@SlackletSlack.onReceiveDirectMessage(req.getMessage(), this@SlackletSlack)
            }
        })
    }

    private var onReceiveDirectMessage: (Message, Slack) -> Unit = { _, _ -> }
    private var onReceiveRepliedMessage: (Message, Slack) -> Unit = { _, _ -> }
    private var onReceiveMessage: (Message, Slack) -> Unit = { _, _ -> }

    private val idToChannel: Map<ChannelId, SlackChannel> by lazy {
        this.service.slackSession.channels.map { it.id to it }.toMap()
    }

    override fun sendTo(channelId: String, text: String) {
        this.service.sendMessageTo(this.idToChannel[channelId], text)
    }

    override fun putAttachmentTo(channelId: String, attachment: Attachment) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun putReactionTo(message: Message, emoticonName: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendDirectMessageTo(user: User, text: String) {
        this.service.sendDirectMessageTo(user.userName, text)
    }

    override fun onReceiveMessage(handler: (message: Message, slack: Slack) -> Unit) {
        this.onReceiveMessage = handler
    }

    override fun onReceiveDirectMessage(handler: (message: Message, slack: Slack) -> Unit) {
        this.onReceiveDirectMessage = handler
    }

    override fun onReceiveRepliedMessage(handler: (message: Message, slack: Slack) -> Unit) {
        this.onReceiveRepliedMessage = handler
    }

    override fun getChannelIdByName(channelName: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startService() {
        this.service.start()
    }

    override fun stopService() {
        this.service.stop()
    }
}