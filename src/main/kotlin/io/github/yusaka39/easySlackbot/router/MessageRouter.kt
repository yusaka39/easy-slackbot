package io.github.yusaka39.easySlackbot.router

import io.github.yusaka39.easySlackbot.slack.Message
import javax.swing.Action

class MessageRouter(handlerSetFactory: HandlerSetFactory) {
    constructor() : this(HandlerSetFactory.default())
    private val handlers: Set<Handler> = handlerSetFactory.create()

    fun findHandlerFor(message: Message, type: Handler.HandlerType): Handler? =
            this.handlers.firstOrNull { it.isMatchTo(message, type) }
}