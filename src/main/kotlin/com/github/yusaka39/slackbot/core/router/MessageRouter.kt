package com.github.yusaka39.slackbot.core.router

import com.github.yusaka39.slackbot.api.entity.Message
import com.github.yusaka39.slackbot.api.handler.Handler

internal interface MessageRouter {
    fun findHandlersFor(message: Message, isRepliedMessage: Boolean): List<Handler>
}
