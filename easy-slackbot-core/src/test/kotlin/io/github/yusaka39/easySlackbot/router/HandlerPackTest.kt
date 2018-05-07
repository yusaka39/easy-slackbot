package io.github.yusaka39.easySlackbot.router

import io.github.yusaka39.easySlackbot.slack.Channel
import io.github.yusaka39.easySlackbot.slack.Message
import io.github.yusaka39.easySlackbot.slack.User
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HandlerPackTest {
    @Test
    fun gettingMessageWorksCorrectly() {
        val testMessage = Message(
            User("Uabcdefg", "awesomekotlin", "John Doe"),
            "kotlin is awesome.",
            Channel("Cabcdefg", "secret_group"),
            "12345678.123456"
        )
        object : HandlerPack() {
            init {
                this._receivedMessage = testMessage
                assertEquals(testMessage, this.receivedMessage)
            }
        }
    }

    @Test
    fun gettingMessageFailsCorrectly() {
        assertFailsWith<IllegalStateException> {
            object : HandlerPack() {
                init {
                    this.receivedMessage
                }
            }
        }
    }
}