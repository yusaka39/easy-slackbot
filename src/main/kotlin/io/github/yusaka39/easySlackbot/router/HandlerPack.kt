package io.github.yusaka39.easySlackbot.router

import io.github.yusaka39.easySlackbot.slack.Message

abstract class HandlerPack() {
    internal var _receivedMessage: Message? = null
    protected val receivedMessage: Message
        get() = this._receivedMessage ?: throw IllegalStateException("Backing property is null")
}