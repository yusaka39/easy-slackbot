package com.github.yusaka39.slackbot.core.router

import com.github.yusaka39.slackbot.api.entity.Message
import com.github.yusaka39.slackbot.api.handler.Handler
import com.github.yusaka39.slackbot.core.lib.logger


internal class MessageRouterImpl(handlerSetFactory: HandlerSetFactory): MessageRouter {
    private val logger by this.logger()
    private val handlers: Set<Handler> = handlerSetFactory.create()

    override fun findHandlersFor(message: Message, isRepliedMessage: Boolean): List<Handler> {
        val handlers = this.handlers.filter { it.isMatchedTo(message, isRepliedMessage) }
        if (handlers.isEmpty()) {
            this.logger.info("Message \"${message.text}\" is not matched to any handler.")
        } else {
            this.logger.info("Message \"${message.text}\" is matched to ${
            handlers.joinToString(", ") { it.javaClass.simpleName }
            }.")
        }
        return handlers
    }
}