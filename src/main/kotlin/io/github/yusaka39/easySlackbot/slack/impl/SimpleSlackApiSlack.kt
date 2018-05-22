package io.github.yusaka39.easySlackbot.slack.impl

import com.ullink.slack.simpleslackapi.SlackChannel
import com.ullink.slack.simpleslackapi.SlackPreparedMessage
import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.events.SlackEventType
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory
import io.github.yusaka39.easySlackbot.api.entity.Attachment
import io.github.yusaka39.easySlackbot.api.entity.Channel
import io.github.yusaka39.easySlackbot.api.entity.Message
import io.github.yusaka39.easySlackbot.api.entity.Slack
import io.github.yusaka39.easySlackbot.lib.LazyMap
import io.github.yusaka39.easySlackbot.lib.logger
import io.github.yusaka39.easySlackbot.lib.toChannel
import io.github.yusaka39.easySlackbot.lib.toMessage
import io.github.yusaka39.easySlackbot.lib.toSlackAttachment


class SimpleSlackApiSlack(slackToken: String) : Slack {
    private val myId by lazy {
        this.session.sessionPersona().id
    }
    private val session: SlackSession = SlackSessionFactory.createWebSocketSlackSession(slackToken).apply {
        this.addMessagePostedListener { slackMessagePosted, _ ->
            if (slackMessagePosted.eventType != SlackEventType.SLACK_MESSAGE_POSTED) {
                return@addMessagePostedListener
            }
            if (this@SimpleSlackApiSlack.myId == slackMessagePosted.user.id) {
                this@SimpleSlackApiSlack.logger.info("Ignore my post.")
                return@addMessagePostedListener
            }
            val message = slackMessagePosted.toMessage()
            when {
                slackMessagePosted.channel.id.startsWith("D") -> {
                    this@SimpleSlackApiSlack.logger.info("Received DM.")
                    this@SimpleSlackApiSlack.onReceiveDirectMessage(message, this@SimpleSlackApiSlack)
                }
                slackMessagePosted.messageContent.contains("<@${this@SimpleSlackApiSlack.myId}>") -> {
                    this@SimpleSlackApiSlack.logger.info("Received a reply.")
                    this@SimpleSlackApiSlack.onReceiveReply(message, this@SimpleSlackApiSlack)
                }
                else -> {
                    this@SimpleSlackApiSlack.logger.info("Received a message.")
                    this@SimpleSlackApiSlack.onReceiveMessage(message, this@SimpleSlackApiSlack)
                }
            }
        }
    }

    private var onReceiveDirectMessage: (Message, Slack) -> Unit = { _, _ -> }
    private var onReceiveReply: (Message, Slack) -> Unit = { _, _ -> }
    private var onReceiveMessage: (Message, Slack) -> Unit = { _, _ -> }

    private val logger by this.logger()

    private val channelIdToChannel by lazy {
        LazyMap<String, SlackChannel>(
                {
                    this.session.channels.fold(mutableMapOf()) { acc, c ->
                        acc.apply { this[c.id] = c }
                    }
                },
                { key -> this.session.findChannelById(key) }
        )
    }

    private val channelNameToChannel by lazy {
        LazyMap<String, SlackChannel>(
                {
                    this.session.channels.fold(mutableMapOf()) { acc, c ->
                        c.name?.let { acc.apply { this[c.name] = c } } ?: acc
                    }
                },
                { key -> this.session.findChannelByName(key) }
        )
    }

    private val userNameToDmChannel by lazy {
        val createMap: () -> MutableMap<String, Channel> = {
            this.session.channels.fold(mutableMapOf()) { acc, c ->
                if (!c.isDirect) {
                    return@fold acc
                }
                this.logger.info(c.members.toString())
                acc.apply { this[c.members.first().userName] = c.toChannel() }
            }
        }

        var cache: Map<String, Channel> = mapOf()

        LazyMap(createMap) { key ->
            val user = this.session.findUserByUserName(key) ?: return@LazyMap null
            cache[user.id]?.let { return@LazyMap it }
            this.session.openDirectMessageChannel(user)
            cache = this.session.fetchDirectMessages(slackToken)
            cache[user.id]
        }
    }

    override fun sendTo(channelId: String, text: String) {
        val message = SlackPreparedMessage.Builder()
                .withMessage(text)
                .build()
        this.session.sendMessage(this.channelIdToChannel[channelId], message)
    }

    override fun putAttachmentTo(channelId: String, vararg attachments: Attachment) {
        val message = SlackPreparedMessage.Builder()
                .withAttachments(attachments.map { it.toSlackAttachment() })
                .build()
        this.session.sendMessage(this.channelIdToChannel[channelId], message)
    }

    override fun putReactionTo(channelId: String, timestamp: String, emoticonName: String) {
        this.session.addReactionToMessage(
                this.channelIdToChannel[channelId],
                timestamp,
                emoticonName
        )
    }

    override fun sendDirectMessageTo(username: String, text: String) {
        val id = this.userNameToDmChannel[username]?.id
                ?: throw IllegalArgumentException("No dm channel for $username is found.")
        this.session.sendMessage(SlackChannel(id, null, null, null, true, true, false), text)
    }

    override fun onReceiveMessage(handler: (message: Message, slack: Slack) -> Unit) {
        this.onReceiveMessage = handler
    }

    override fun onReceiveDirectMessage(handler: (message: Message, slack: Slack) -> Unit) {
        this.onReceiveDirectMessage = handler
    }

    override fun onReceiveReply(handler: (message: Message, slack: Slack) -> Unit) {
        this.onReceiveReply = handler
    }

    override fun getChannelIdOrNullByName(channelName: String): String? =
            this.channelNameToChannel[channelName]?.id

    override fun getUserIdByName(userName: String): String? =
            this.session.users.firstOrNull { it.userName == userName }?.id

    override fun getDmChannelIdOrNullByUserName(username: String): String? =
            this.userNameToDmChannel[username]?.id

    override fun startService() {
        this.logger.info("Connecting to slack.")
        this.session.connect()
        this.logger.info("Connected")
    }

    override fun stopService() {
        this.logger.info("Disconnecting from slack.")
        this.session.disconnect()
        this.logger.info("Disconnected")
    }

}
