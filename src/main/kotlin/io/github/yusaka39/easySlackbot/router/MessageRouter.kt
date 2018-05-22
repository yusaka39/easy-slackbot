package io.github.yusaka39.easySlackbot.router

import io.github.yusaka39.easySlackbot.api.entity.Message
import io.github.yusaka39.easySlackbot.api.handler.Handler

internal interface MessageRouter {
    fun findHandlersFor(message: Message, isRepliedMessage: Boolean): List<Handler>
}
