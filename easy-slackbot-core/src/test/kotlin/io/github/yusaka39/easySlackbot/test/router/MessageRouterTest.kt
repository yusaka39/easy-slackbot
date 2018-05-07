package io.github.yusaka39.easySlackbot.test.router

import io.github.yusaka39.easySlackbot.router.Handler
import io.github.yusaka39.easySlackbot.router.HandlerPack
import io.github.yusaka39.easySlackbot.router.HandlerSetFactory
import io.github.yusaka39.easySlackbot.router.HandlerType
import io.github.yusaka39.easySlackbot.router.MessageRouter
import io.github.yusaka39.easySlackbot.router.actions.Action
import io.github.yusaka39.easySlackbot.slack.ChannelImpl
import io.github.yusaka39.easySlackbot.slack.MessageImpl
import io.github.yusaka39.easySlackbot.slack.UserImpl
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageRouterTest {
    private class HandlerPackMock : HandlerPack() {
        fun handler(): Action {
            TODO()
        }
    }

    private val testMessage = MessageImpl(UserImpl("foo", "bar", "baz"), "foobar", ChannelImpl("foo", "bar"), "12345")

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