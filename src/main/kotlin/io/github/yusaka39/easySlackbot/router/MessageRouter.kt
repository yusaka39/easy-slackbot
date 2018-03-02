package io.github.yusaka39.easySlackbot.router

import io.github.yusaka39.easySlackbot.slack.Message

class MessageRouter(handlerSetFactory: HandlerSetFactory) {
    private val handlers: Set<Handler> = handlerSetFactory.create()

    fun findHandlerFor(message: Message, type: HandlerType): Handler? =
            this.handlers.firstOrNull { it.isMatchTo(message, type) }
}