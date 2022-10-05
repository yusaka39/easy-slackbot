package io.github.yusaka39.easySlackbot.slack.impl

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer.None
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.slack.api.Slack
import com.slack.api.bolt.App
import com.slack.api.bolt.AppConfig
import com.slack.api.model.Action
import com.slack.api.model.ConversationType
import com.slack.api.model.Field
import io.github.yusaka39.easySlackbot.lib.logger
import io.github.yusaka39.easySlackbot.slack.Attachment
import io.github.yusaka39.easySlackbot.slack.Channel
import io.github.yusaka39.easySlackbot.slack.Message
import io.github.yusaka39.easySlackbot.slack.User
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import io.github.yusaka39.easySlackbot.slack.Slack as S

class RtmApiSlack(private val token: String) : S {
    private val stateLock = ReentrantLock()
    private var isRunning = false
    private val logger by this.logger()

    private val slack by lazy {
        App(AppConfig.builder().singleTeamBotToken(token).build())
    }

    private val selfBotId by lazy {
        slack.client.authTest { it }.botId
    }

    private val mapper = ObjectMapper()
    private val rtm by lazy {
        Slack.getInstance().rtmConnect(token).also { rtm ->
            rtm.addMessageHandler {
                val msg = mapper.readValue(it, RtmMessage::class.java)
                when (msg) {
                    is RtmMessage.ChatMessage -> processMessage(msg.toMessage())
                    is RtmMessage.GoodbyeMessage -> stateLock.withLock {
                        if (isRunning) rtm.reconnect()
                    }
                    else -> Unit
                }
            }
            rtm.addCloseHandler { it.closeCode }
        }
    }

    private fun processMessage(msg: Message) {
        if (selfBotId == msg.user.id) {
            logger.info("Ignore my post.")
        }
        when {
            msg.channel.id.startsWith("D") -> {
                logger.info("Received DM.")
                onReceiveDirectMessage(msg, this)
            }
            msg.text.contains("<@${selfBotId}>") -> {
                logger.info("Received a reply.")
                onReceiveReply(msg, this)
            }
            else -> {
                logger.info("Received a message.")
                onReceiveMessage(msg, this)
            }
        }
    }

    @JsonDeserialize(using = RtmMessage.Deserializer::class)
    private sealed class RtmMessage(@field:JsonProperty val type: String) {
        class Deserializer : StdDeserializer<RtmMessage>(null as Class<*>?) {
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): RtmMessage =
                p.use {
                    val node: JsonNode = p.codec.readTree(p)
                    val type: String = node.get("type").asText()
                    return when (type) {
                        "message" -> ObjectMapper().readValue(
                            node.toString(), ChatMessage::class.java
                        )
                        "goodbye" -> GoodbyeMessage()
                        else -> UnknownMessage(type)
                    }
                }
        }

        class UnknownMessage(type: String) : RtmMessage(type)
        class GoodbyeMessage : RtmMessage("goodbye")

        @JsonDeserialize(using = None::class)
        @JsonIgnoreProperties(ignoreUnknown = true)
        class ChatMessage @JsonCreator constructor(
            @JsonProperty("type") type: String,
            @param:JsonProperty("user") val userId: String?,
            @param:JsonProperty("bot_id") val botId: String?,
            @param:JsonProperty("app_id") val appId: String?,
            @param:JsonProperty("channel") val channelId: String,
            @param:JsonProperty("text") val text: String?,
            @param:JsonProperty("event_ts") val eventTs: String,
            @param:JsonProperty("ts") val ts: String,
        ) : RtmMessage(type)
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
            var cur: String? = null
            do {
                slack.client.conversationsList {
                    it.cursor(cur).types(ConversationType.values().toList())
                }.let {
                    cur = it.responseMetadata.nextCursor
                    this.addAll(it.channels)
                }
            } while (!cur.isNullOrEmpty())
        }
    }

    private fun RtmMessage.ChatMessage.toMessage(): Message {
        val user = userId?.let {
            users.first { it.id == userId }
        }?.let {
            User(it.id, it.name, it.realName)
        } ?: User(botId ?: "", "", "")

        val channel = channels.first { it.id == channelId }.let {
            Channel(it.id, it.name)
        }
        return Message(user, text ?: "", channel, ts)
    }

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
            it.text("").channel(channelId).attachments(
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
                        .title(it.title)
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

    override fun startService() = this.stateLock.withLock {
        this.isRunning = true
        this.logger.info("Connecting to slack.")
        this.logger.info("Creating user cache......")
        val _user = users
        this.logger.info("Done.")
        this.logger.info("Creating channel cache......")
        val _channels = channels
        this.logger.info("Done.")
        this.rtm.connect()
        this.logger.info("Connected")
    }

    override fun stopService() = this.stateLock.withLock {
        this.isRunning = false
        this.logger.info("Disconnecting from slack.")
        this.rtm.disconnect()
        this.logger.info("Disconnected")
    }
}
