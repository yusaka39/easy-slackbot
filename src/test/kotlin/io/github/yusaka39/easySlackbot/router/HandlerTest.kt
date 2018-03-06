package io.github.yusaka39.easySlackbot.router

import io.github.yusaka39.easySlackbot.annotations.GroupParam
import io.github.yusaka39.easySlackbot.router.actions.Action
import io.github.yusaka39.easySlackbot.slack.Channel
import io.github.yusaka39.easySlackbot.slack.Message
import io.github.yusaka39.easySlackbot.slack.Slack
import io.github.yusaka39.easySlackbot.slack.User
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HandlerTest {

    private val testMessage = Message(
        User("U123456", "john", "John Doe"),
        "This is message",
        Channel("C123456", "foobar"),
        "12345678.123456"
    )

    @Test
    fun isMatchToWorksFine() {
        val handler = Handler(
            HandlerTest::class, HandlerTest::isMatchToWorksFine,
            "^This.*".toRegex(), HandlerType.ListenTo
        )
        assertTrue(handler.isMatchTo(this.testMessage, HandlerType.ListenTo))
        assertFalse(handler.isMatchTo(this.testMessage, HandlerType.RespondTo))
        assertFalse(handler.isMatchTo(this.testMessage.copy(text = "That is message"), HandlerType.ListenTo))
    }

    @Test
    fun toStringWorksFine() {
        val handler = Handler(
            HandlerTest::class, HandlerTest::toStringWorksFine,
            "^This.*".toRegex(), HandlerType.ListenTo
        )
        assertEquals(
            "Handler[regex: ^This.*, function: " +
                    "fun io.github.yusaka39.easySlackbot.router.HandlerTest.toStringWorksFine(): kotlin.Unit]",
            handler.toString()
        )
        handler.toString()
    }


    @Test
    fun generateActionForMessageWorksFine() {
        var action = Handler(
            LegalHandlerPack::class, LegalHandlerPack::returnsActionWithoutArgument,
            "^This.*".toRegex(), HandlerType.ListenTo
        ).generateActionForMessage(this.testMessage)
        assertTrue(action === aInstance)

        action = Handler(
            LegalHandlerPack::class, LegalHandlerPack::returnsActionWithArguments,
            """^(\d+)\s+(\d+)$""".toRegex(), HandlerType.ListenTo
        ).generateActionForMessage(this.testMessage.copy(text = "100  200"))
        (action as? ActionWithInts)?.let {
            assertEquals(100, it.a)
            assertEquals(200, it.b)
        } ?: assertTrue(false)

        action = Handler(
            HandlerPackWithoutPrimaryConstructor::class,
            HandlerPackWithoutPrimaryConstructor::returnsActionWithoutArgument,
            "^This.*".toRegex(), HandlerType.ListenTo
        ).generateActionForMessage(this.testMessage)
        assertTrue(action === aInstance)
    }

    @Test
    fun generateActionForMessageFailsCorrectly() {
        assertFailsWith<IllegalStateException> {
            Handler(
                LegalHandlerPack::class, LegalHandlerPack::returnsNonAction,
                "^This.*".toRegex(), HandlerType.ListenTo
            ).generateActionForMessage(this.testMessage)
        }
        assertFailsWith<IllegalStateException> {
            Handler(
                LegalHandlerPack::class, LegalHandlerPack::returnsNonAction,
                "^This.*".toRegex(), HandlerType.ListenTo
            ).generateActionForMessage(this.testMessage.copy(text = "That is text"))
        }
        assertFailsWith<IllegalStateException> {
            Handler(
                LegalHandlerPack::class, LegalHandlerPack::returnsActionWithoutAnnotatedArguments,
                """^(\d+)\s+(\d+)$""".toRegex(), HandlerType.ListenTo
            ).generateActionForMessage(this.testMessage.copy(text = "100  200"))
        }
        assertFailsWith<IllegalStateException> {
            Handler(
                IllegalHandlerPack::class, IllegalHandlerPack::returnsActionWithoutArgument,
                "^This.*".toRegex(), HandlerType.ListenTo
            ).generateActionForMessage(this.testMessage)
        }
        assertFailsWith<IllegalStateException> {
            Handler(
                YetAnotherHandlerPack::class, YetAnotherHandlerPack::returnsActionWithoutArgument,
                "^This.*".toRegex(), HandlerType.ListenTo
            ).generateActionForMessage(this.testMessage)
        }
    }
}

private class NopAction : Action {
    override fun run(slack: Slack) {}
}

private class ActionWithInts(val a: Int, val b: Int): Action {
    override fun run(slack: Slack) {}
}

private val aInstance = NopAction()

class LegalHandlerPack : HandlerPack() {
    fun returnsActionWithoutArgument(): Action {
        return aInstance
    }

    fun returnsActionWithArguments(@GroupParam(1) a: Int, @GroupParam(2) b: Int): Action {
        return ActionWithInts(a, b)
    }

    fun returnsActionWithoutAnnotatedArguments(a: Int, b: Int): Action {
        return ActionWithInts(a, b)
    }

    fun returnsNonAction() {}
}

class IllegalHandlerPack(id: String) : HandlerPack() {
    fun returnsActionWithoutArgument(): Action {
        return aInstance
    }
}

class HandlerPackWithoutPrimaryConstructor(val id: String) : HandlerPack() {
    constructor() : this("id")
    fun returnsActionWithoutArgument(): Action {
        return aInstance
    }
}

class YetAnotherHandlerPack {
    fun returnsActionWithoutArgument(): Action {
        return aInstance
    }
}