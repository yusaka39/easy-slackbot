package io.github.yusaka39.easySlackbot.router

import io.github.yusaka39.easySlackbot.api.handler.Handler

internal interface HandlerSetFactory {
    fun create(): Set<Handler>
}
