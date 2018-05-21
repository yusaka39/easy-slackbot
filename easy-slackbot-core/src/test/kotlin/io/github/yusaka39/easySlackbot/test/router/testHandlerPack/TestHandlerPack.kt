package io.github.yusaka39.easySlackbot.test.router.testHandlerPack

import io.github.yusaka39.easySlackbot.api.entity.Action
import io.github.yusaka39.easySlackbot.api.entity.Slack
import io.github.yusaka39.easySlackbot.router.HandlerPack
import io.github.yusaka39.easySlackbot.router.HandlerType

class TestHandlerPack : HandlerPack() {
    private class NopAction : Action {
        override fun run(slack: Slack) {}
    }

    @HandlerFunction("foo")
    fun foo(): Action = NopAction()

    @HandlerFunction("bar", type = [HandlerType.ListenTo])
    fun bar(): Action = NopAction()

    @HandlerFunction("foobar", type = [HandlerType.RespondTo])
    fun foobar(): Action = NopAction()
}
