package io.github.yusaka39.easySlackbot.api.handler

import io.github.yusaka39.easySlackbot.api.entity.Action
import io.github.yusaka39.easySlackbot.api.entity.Message

interface Handler {
    fun createActionProvider(message: Message): () -> Action
    fun isMatchedTo(message: Message): Boolean
}