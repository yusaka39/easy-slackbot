package io.github.yusaka39.easySlackbot.router

interface HandlerSetFactory {
    fun create(): Set<Handler>
}
