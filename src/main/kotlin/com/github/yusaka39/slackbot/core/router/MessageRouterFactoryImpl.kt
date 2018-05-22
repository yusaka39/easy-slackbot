package com.github.yusaka39.slackbot.core.router

internal class MessageRouterFactoryImpl(
        private val handlerSetFactory: HandlerSetFactory
) : MessageRouterFactory {
    override fun create(): MessageRouter = MessageRouterImpl(this.handlerSetFactory)
}