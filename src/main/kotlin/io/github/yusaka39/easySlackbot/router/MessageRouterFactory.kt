package io.github.yusaka39.easySlackbot.router

interface MessageRouterFactory {
    fun create(): MessageRouter
}