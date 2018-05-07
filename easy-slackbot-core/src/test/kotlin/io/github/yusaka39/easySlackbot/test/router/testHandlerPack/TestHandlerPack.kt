package io.github.yusaka39.easySlackbot.test.router.testHandlerPack

import io.github.yusaka39.easySlackbot.annotations.HandlerFunction
import io.github.yusaka39.easySlackbot.router.HandlerPack
import io.github.yusaka39.easySlackbot.router.HandlerType
import io.github.yusaka39.easySlackbot.router.actions.Action
import io.github.yusaka39.easySlackbot.slack.Slack

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
