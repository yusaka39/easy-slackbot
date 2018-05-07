package io.github.yusaka39.easySlackbot.router

internal class AnnotationBasedMessageRouterFactory(private val searchPackage: String) : MessageRouterFactory {
    override fun create(): MessageRouter = MessageRouter(AnnotationBasedHandlerSetFactory(this.searchPackage))
}