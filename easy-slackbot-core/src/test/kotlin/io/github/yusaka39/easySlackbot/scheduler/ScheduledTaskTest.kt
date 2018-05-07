package io.github.yusaka39.easySlackbot.scheduler

import io.github.yusaka39.easySlackbot.annotations.RunWithInterval
import io.github.yusaka39.easySlackbot.router.HandlerPack
import io.github.yusaka39.easySlackbot.router.actions.Action
import io.github.yusaka39.easySlackbot.slack.Slack
import java.util.concurrent.TimeUnit
import kotlin.reflect.KCallable
import kotlin.reflect.full.findAnnotation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ScheduledTaskTest {
    private class TestClass : HandlerPack() {
        @RunWithInterval(9, 12, "UTC", 100, TimeUnit.MILLISECONDS)
        fun legalFunction(): Action = TestClass.nop

        @RunWithInterval(9, 12, "UTC", 100, TimeUnit.MILLISECONDS)
        fun illegalFunction(): Any = Any()

        companion object {
            val nop = object : Action {
                override fun run(slack: Slack) {}
            }
        }
    }

    @Test
    fun toStringWorksFine() {
        val callable = TestClass::legalFunction
        val task = ScheduledTask(
            TestClass::class, callable, Schedule(callable.findAnnotation() ?: throw AssertionError())
        )
        assertEquals(
            "ScheduledTask[ startedAt: 09:12, function: $callable ]",
            task.toString()
        )
    }

    @Test
    fun getActionWorksFine() {
        var callable: KCallable<*> = TestClass::legalFunction
        val legalTask = ScheduledTask(
            TestClass::class, callable, Schedule(callable.findAnnotation() ?: throw AssertionError())
        )
        callable = TestClass::illegalFunction
        val illegalTask = ScheduledTask(
            TestClass::class, callable, Schedule(callable.findAnnotation() ?: throw AssertionError())
        )
        assertEquals(TestClass.nop, legalTask.getAction())
        assertFailsWith<IllegalStateException> {
            illegalTask.getAction()
        }
    }
}