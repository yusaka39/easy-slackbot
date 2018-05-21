package io.github.yusaka39.easySlackbot.test.bot.testHandlerPack

import io.github.yusaka39.easySlackbot.api.entity.Action
import io.github.yusaka39.easySlackbot.router.HandlerPack
import io.github.yusaka39.easySlackbot.router.actions.PostAction

class TestHandlerPack : HandlerPack() {
    @HandlerFunction("^foo.*")
    fun foo(): Action = PostAction("channelId", "text")
}