package io.github.yusaka39.easySlackbot.router

internal interface HandlerSetFactory {
    fun create(): Set<Handler>
}
