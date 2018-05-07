package io.github.yusaka39.easySlackbot.test.scheduler.testTaskHolder

import io.github.yusaka39.easySlackbot.annotations.RunWithInterval
import io.github.yusaka39.easySlackbot.router.HandlerPack
import io.github.yusaka39.easySlackbot.api.entity.Action
import io.github.yusaka39.easySlackbot.api.entity.Slack
import java.util.concurrent.TimeUnit

private val nop = object : Action {
    override fun run(slack: Slack) {}
}

class TestTaskHolder : HandlerPack() {
    @RunWithInterval(10, 20, "PST", 30, TimeUnit.HOURS)
    fun foo(): Action = nop

    @RunWithInterval(2, 4, "PST", 8, TimeUnit.HOURS)
    fun bar(): Action = nop

    @RunWithInterval(3, 6, "PST", 9, TimeUnit.HOURS)
    fun baz(): Action = nop
}