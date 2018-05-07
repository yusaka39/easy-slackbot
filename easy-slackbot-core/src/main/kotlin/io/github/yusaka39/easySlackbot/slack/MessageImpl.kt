package io.github.yusaka39.easySlackbot.slack

import io.github.yusaka39.easySlackbot.api.entity.Attachment
import io.github.yusaka39.easySlackbot.api.entity.Channel
import io.github.yusaka39.easySlackbot.api.entity.Message
import io.github.yusaka39.easySlackbot.api.entity.User

data class MessageImpl(
        override val user: User,
        override val text: String,
        override val channel: Channel,
        override val timestamp: String,
        override val attachments: List<Attachment> = listOf()
) : Message