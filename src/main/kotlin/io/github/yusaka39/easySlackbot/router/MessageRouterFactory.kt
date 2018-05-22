package io.github.yusaka39.easySlackbot.router

internal interface MessageRouterFactory {
    fun create(): MessageRouter
}