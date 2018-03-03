package io.github.yusaka39.easySlackbot.router

import io.github.yusaka39.easySlackbot.lib.Log
import io.github.yusaka39.easySlackbot.slack.Message

class MessageRouter(handlerSetFactory: HandlerSetFactory) {
    private val handlers: Set<Handler> = handlerSetFactory.create()

    fun findHandlerFor(message: Message, type: HandlerType): Handler? {
        val handler = this.handlers.firstOrNull { it.isMatchTo(message, type) }
        if (handler != null) {
            Log.d("Message \"${message.text}\" is matched to $handler.")
        } else {
            Log.d("Message \"${message.text}\" is not matched to any handler.")
        }
        return handler
    }

}