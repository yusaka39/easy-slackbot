package io.github.yusaka39.easySlackbot.router

import io.github.yusaka39.easySlackbot.api.entity.Message
import io.github.yusaka39.easySlackbot.api.handler.Handler
import io.github.yusaka39.easySlackbot.lib.logger


internal class MessageRouterImpl(handlerSetFactory: HandlerSetFactory): MessageRouter {
    private val logger by this.logger()
    private val handlers: Set<Handler> = handlerSetFactory.create()

    override fun findHandlersFor(message: Message, isRepliedMessage: Boolean): List<Handler> {
        val handlers = this.handlers.filter { it.isMatchedTo(message, isRepliedMessage) }
        if (handlers.isEmpty()) {
            this.logger.info("Message \"${message.text}\" is matched to ${
                handlers.joinToString(", ") { it.javaClass.simpleName }
            }.")
        } else {
            this.logger.info("Message \"${message.text}\" is not matched to any handler.")
        }
        return handlers
    }
}