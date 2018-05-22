package com.github.yusaka39.slackbot.core.router

import com.github.yusaka39.slackbot.api.handler.Handler

internal interface HandlerSetFactory {
    fun create(): Set<Handler>
}
