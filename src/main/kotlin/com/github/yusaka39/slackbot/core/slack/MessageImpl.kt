package com.github.yusaka39.slackbot.core.slack

import com.github.yusaka39.slackbot.api.entity.Attachment
import com.github.yusaka39.slackbot.api.entity.Channel
import com.github.yusaka39.slackbot.api.entity.Message
import com.github.yusaka39.slackbot.api.entity.User

data class MessageImpl(
        override val user: User,
        override val text: String,
        override val channel: Channel,
        override val timestamp: String,
        override val attachments: List<Attachment> = listOf()
) : Message