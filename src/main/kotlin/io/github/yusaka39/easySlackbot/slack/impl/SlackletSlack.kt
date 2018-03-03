package io.github.yusaka39.easySlackbot.slack.impl

import io.github.yusaka39.easySlackbot.lib.LazyMap
import io.github.yusaka39.easySlackbot.lib.getMessage
import io.github.yusaka39.easySlackbot.lib.toChannel
import io.github.yusaka39.easySlackbot.lib.toSlackAttachment
import io.github.yusaka39.easySlackbot.slack.*
import org.riversun.slacklet.Slacklet
import org.riversun.slacklet.SlackletRequest
import org.riversun.slacklet.SlackletResponse
import org.riversun.slacklet.SlackletService
import org.riversun.xternal.simpleslackapi.SlackChannel

private typealias ChannelId = String
private typealias ChannelName = String
private typealias UserName = String

class SlackletSlack(slackToken: String) : Slack {
    private val service = SlackletService(slackToken).apply {
        this.addSlacklet(object : Slacklet() {
            override fun onMentionedMessagePosted(req: SlackletRequest, resp: SlackletResponse) {
                req.rawPostedMessage.timestamp
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

    private val idToChannel: LazyMap<ChannelId, SlackChannel> by lazy {
        LazyMap(
            { this.service.slackSession.channels.map { it.id to it }.toMap() },
            { key -> this.service.slackSession.channels.firstOrNull { it.id == key } }
        )
    }

    private val nameToChannel: LazyMap<ChannelName, SlackChannel> by lazy {
        LazyMap(
            { this.service.slackSession.channels.map { it.name to it }.toMap() },
            { key -> this.service.slackSession.channels.firstOrNull { it.name == key }}
        )
    }

    private fun createDbChannelMap(): Map<UserName, SlackChannel> {
        val userNames = this.service.slackSession.users.map { it.userName }
        val channels = this.service.slackSession.channels
        return userNames.zip(channels).toMap()
    }

    private val usernameToDmChannel: LazyMap<UserName, SlackChannel> by lazy {
        LazyMap(
            this::createDbChannelMap,
            { it -> this.createDbChannelMap()[it] }
        )
    }

    override fun sendTo(channelId: String, text: String) {
        this.service.sendMessageTo(this.idToChannel[channelId], text)
    }

    override fun putAttachmentTo(channelId: String, attachment: Attachment) {
        this.service.sendMessageTo(this.idToChannel[channelId], null, attachment.toSlackAttachment())
    }

    override fun putReactionTo(channelId: String, timestamp: String, emoticonName: String) {
        print(this.nameToChannel)
        this.service.slackSession.addReactionToMessage(this.idToChannel[channelId],
                                                       timestamp,
                                                       emoticonName)
    }

    override fun sendDirectMessageTo(username: String, text: String) {
        this.service.sendDirectMessageTo(username, text)
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

    private fun  getChannelOrNullByName(channelName: ChannelName): Channel? =
            this.nameToChannel[channelName]?.toChannel()

    override fun getChannelIdOrNullByName(channelName: ChannelName): String? =
            getChannelOrNullByName(channelName)?.id

    override fun getDmChannelIdOrNullByUserName(username: String): String? =
            this.usernameToDmChannel[username]?.toChannel()?.id

    override fun startService() {
        this.service.start()
    }

    override fun stopService() {
        this.service.stop()
    }
}