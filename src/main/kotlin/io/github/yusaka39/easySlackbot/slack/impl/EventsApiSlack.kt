package io.github.yusaka39.easySlackbot.slack.impl

import com.slack.api.bolt.App
import com.slack.api.bolt.AppConfig
import com.slack.api.bolt.socket_mode.SocketModeApp
import com.slack.api.model.Action
import com.slack.api.model.Field
import com.slack.api.model.event.MessageEvent
import io.github.yusaka39.easySlackbot.lib.logger
import io.github.yusaka39.easySlackbot.slack.Attachment
import io.github.yusaka39.easySlackbot.slack.Channel
import io.github.yusaka39.easySlackbot.slack.Message
import io.github.yusaka39.easySlackbot.slack.User
import io.github.yusaka39.easySlackbot.slack.Slack as S

// TODO: Not implemented
private class EventsApiSlack(private val token: String) : S {
    private val logger by this.logger()

    private val slack by lazy {
        App(
            AppConfig.builder()
                .singleTeamBotToken(token)
                .build()
        ).event(MessageEvent::class.java) { payload, ctx ->
            val myId = ctx.botUserId
            val ev = payload.event
            if (myId == ev.user) {
                logger.info("Ignore my post.")
                return@event ctx.ack()
            }
            val message = ev.toMessage()
            when {
                message.channel.id.startsWith("D") -> {
                    logger.info("Received DM.")
                    onReceiveDirectMessage(message, this)
                }
                message.text.contains("<@${myId}>") -> {
                    logger.info("Received a reply.")
                    onReceiveReply(message, this)
                }
                else -> {
                    logger.info("Received a message.")
                    onReceiveMessage(message, this)
                }
            }
            ctx.ack()
        }
    }

    private val users: List<com.slack.api.model.User> by lazy {
        mutableListOf<com.slack.api.model.User>().apply {
            var cur: String? = null
            do {
                slack.client.usersList {
                    it.limit(100).cursor(cur)
                }.let {
                    cur = it.responseMetadata.nextCursor
                    this.addAll(it.members)
                }
            } while (!cur.isNullOrEmpty())
        }
    }

    private val channels: List<com.slack.api.model.Conversation> by lazy {
        mutableListOf<com.slack.api.model.Conversation>().apply {
            do {
                var cur: String? = null
                slack.client.conversationsList { it.cursor(cur) }.let {
                    cur = it.responseMetadata.nextCursor
                    this.addAll(it.channels)
                }
            } while (!cur.isNullOrEmpty())

        }
    }

    private fun MessageEvent.toMessage() = Message(
        fetchUser(), text, fetchChannel(), ts
    )

    // TODO
    private fun MessageEvent.fetchUser() = User(user, "", "")
    private fun MessageEvent.fetchChannel() = Channel(channel, "")

    private var onReceiveDirectMessage: (Message, S) -> Unit = { _, _ -> }
    private var onReceiveReply: (Message, S) -> Unit = { _, _ -> }
    private var onReceiveMessage: (Message, S) -> Unit = { _, _ -> }

    override fun sendTo(channelId: String, text: String) {
        slack.client.chatPostMessage {
            it.channel(channelId).text(text)
        }
    }

    override fun putAttachmentTo(channelId: String, vararg attachments: Attachment) {
        slack.client.chatPostMessage {
            it.channel(channelId).attachments(
                attachments.map {
                    com.slack.api.model.Attachment.builder()
                        .actions(
                            it.actions.map { (type, text, url, style) ->
                                Action.builder()
                                    .text(text)
                                    .type(Action.Type.valueOf(type))
                                    .style(style)
                                    .url(url)
                                    .build()
                            }
                        )
                        .fields(
                            it.fields.map { (title, value, isShort) ->
                                Field.builder()
                                    .title(title)
                                    .value(value)
                                    .valueShortEnough(isShort)
                                    .build()
                            }
                        )
                        .titleLink(it.titleLink)
                        .thumbUrl(it.thumbnailUrl)
                        .authorName(it.authorName)
                        .authorLink(it.authorLink)
                        .authorIcon(it.authorIcon)
                        .footer(it.footer)
                        .footerIcon(it.footerIcon)
                        .imageUrl(it.imageUrl)
                        .color(it.color)
                        .build()
                }
            )
        }
    }

    override fun putReactionTo(channelId: String, timestamp: String, emoticonName: String) {
        slack.client.reactionsAdd {
            it.channel(channelId)
                .timestamp(timestamp)
                .name(emoticonName)
        }
    }

    override fun sendDirectMessageTo(username: String, text: String) {
        getChannelIdOrNullByName(username)?.let { sendTo(it, text) }
    }

    override fun onReceiveMessage(handler: (message: Message, slack: S) -> Unit) {
        onReceiveMessage = handler
    }

    override fun onReceiveDirectMessage(handler: (message: Message, slack: S) -> Unit) {
        onReceiveDirectMessage = handler
    }

    override fun onReceiveReply(handler: (message: Message, slack: S) -> Unit) {
        onReceiveReply = handler
    }

    override fun getChannelIdOrNullByName(channelName: String): String? =
        channels.firstOrNull { it.name == channelName }?.id

    override fun getUserIdByName(userName: String): String? =
        users.firstOrNull { it.name == userName }?.id

    override fun getDmChannelIdOrNullByUserName(username: String): String? =
        getUserIdByName(username)?.let { targetId ->
            slack.client.conversationsOpen {
                it.users(listOf(targetId))
            }.channel.id
        }

    override fun startService() {
        this.logger.info("Connecting to slack.")
        SocketModeApp(this.slack).start()
        this.logger.info("Connected")
    }

    override fun stopService() {
        this.logger.info("Disconnecting from slack.")
        this.slack.stop()
        this.logger.info("Disconnected")
    }
}
