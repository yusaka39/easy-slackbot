package io.github.yusaka39.easySlackbot.router

import io.github.yusaka39.easySlackbot.lib.logger
import io.github.yusaka39.easySlackbot.slack.Message

internal class MessageRouter(handlerSetFactory: HandlerSetFactory) {
    private val logger by this.logger()
    private val handlers: Set<Handler> = handlerSetFactory.create()

    fun findHandlerFor(message: Message, type: HandlerType): Handler? {
        val handler = this.handlers.firstOrNull { it.isMatchTo(message, type) }
        if (handler != null) {
            this.logger.info("Message \"${message.text}\" is matched to $handler.")
        } else {
            this.logger.info("Message \"${message.text}\" is not matched to any handler.")
        }
        return handler
    }

}