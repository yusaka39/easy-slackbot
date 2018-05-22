package com.github.yusaka39.slackbot.core.router

internal interface MessageRouterFactory {
    fun create(): MessageRouter
}