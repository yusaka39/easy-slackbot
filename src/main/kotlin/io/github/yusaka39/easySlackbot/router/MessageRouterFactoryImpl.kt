package io.github.yusaka39.easySlackbot.router

internal class MessageRouterFactoryImpl(
        private val handlerSetFactory: HandlerSetFactory
) : MessageRouterFactory {
    override fun create(): MessageRouter = MessageRouterImpl(this.handlerSetFactory)
}