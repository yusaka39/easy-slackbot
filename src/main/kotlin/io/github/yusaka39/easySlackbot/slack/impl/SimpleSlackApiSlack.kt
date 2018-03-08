package io.github.yusaka39.easySlackbot.slack.impl

import com.ullink.slack.simpleslackapi.SlackPreparedMessage
import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.events.EventType
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory
import io.github.yusaka39.easySlackbot.lib.LazyMap
import io.github.yusaka39.easySlackbot.lib.logger
import io.github.yusaka39.easySlackbot.lib.toMessage
import io.github.yusaka39.easySlackbot.lib.toSlackAttachment
import io.github.yusaka39.easySlackbot.slack.Attachment
import io.github.yusaka39.easySlackbot.slack.Message
import io.github.yusaka39.easySlackbot.slack.Slack


class SimpleSlackApiSlack(slackToken: String) : Slack {
    private val session: SlackSession = SlackSessionFactory.createWebSocketSlackSession(slackToken).apply {
        this.addMessagePostedListener { slackMessagePosted, slackSession ->
            if (slackMessagePosted.eventType != EventType.MESSAGE) {
                return@addMessagePostedListener
            }
            val message = slackMessagePosted.toMessage()

            when {
                slackMessagePosted.channel.id.startsWith("D") ->
                    this@SimpleSlackApiSlack.onReceiveDirectMessage(message, this@SimpleSlackApiSlack)
                slackMessagePosted.messageSubType == SlackMessagePosted.MessageSubType.MESSAGE_REPLIED ->
                    this@SimpleSlackApiSlack.onReceiveRepliedMessage(message, this@SimpleSlackApiSlack)
                else -> this@SimpleSlackApiSlack.onReceiveMessage(message, this@SimpleSlackApiSlack)
            }
        }
    }

    private var onReceiveDirectMessage: (Message, Slack) -> Unit = { _, _ -> }
    private var onReceiveRepliedMessage: (Message, Slack) -> Unit = { _, _ -> }
    private var onReceiveMessage: (Message, Slack) -> Unit = { _, _ -> }

    private val logger by this.logger()

    private val channelIdToChannel by lazy {
        LazyMap(
            { this.session.channels.map { it.id to it }.toMap() },
            { key -> this.session.channels.firstOrNull { it.id == key } }
        )
    }

    private val channelNameToChannel by lazy {
        LazyMap(
            { this.session.channels.map { it.name to it }.toMap() },
            { key -> this.session.channels.firstOrNull { it.id == key } }
        )
    }

    private val userNameToDmChannel by lazy {
        val createMap = {
            this.session.users.map { it.userName }.zip(this.session.channels.filter { it.id.startsWith("D") }).toMap()
        }
        LazyMap(
            createMap,
            { key -> createMap()[key] }
        )
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
        this.session.sendMessage(this.userNameToDmChannel[username], text)
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

    override fun getChannelIdOrNullByName(channelName: String): String? =
        this.channelNameToChannel[channelName]?.id

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