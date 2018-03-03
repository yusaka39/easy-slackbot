package io.github.yusaka39.easySlackbot.router

import io.github.yusaka39.easySlackbot.router.actions.Action
import io.github.yusaka39.easySlackbot.slack.Channel
import io.github.yusaka39.easySlackbot.slack.Message
import io.github.yusaka39.easySlackbot.slack.User
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageRouterTest {
    private class HandlerPackMock : HandlerPack() {
        fun handler(): Action { TODO() }
    }

    private val testMessage = Message(User("foo", "bar", "baz"), "foobar", Channel("foo", "bar"), "12345")

    @Test
    fun findHandlerForWorksCorrectly() {
        val handler = Handler(
            HandlerPackMock::class, HandlerPackMock::handler,
            "^foo.*".toRegex(), HandlerType.ListenTo
        )
        val handlerSetFactory = object : HandlerSetFactory {
            override fun create(): Set<Handler> = setOf(handler)
        }

        val router = MessageRouter(handlerSetFactory)
        assertEquals(handler, router.findHandlerFor(this.testMessage, HandlerType.ListenTo))
        assertEquals(null, router.findHandlerFor(this.testMessage, HandlerType.RespondTo))
        assertEquals(null, router.findHandlerFor(this.testMessage.copy(text = "bar"), HandlerType.ListenTo))
    }
}