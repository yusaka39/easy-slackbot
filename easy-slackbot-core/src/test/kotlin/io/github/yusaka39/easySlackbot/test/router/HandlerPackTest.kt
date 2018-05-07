package io.github.yusaka39.easySlackbot.test.router

import io.github.yusaka39.easySlackbot.router.HandlerPack
import io.github.yusaka39.easySlackbot.slack.ChannelImpl
import io.github.yusaka39.easySlackbot.slack.MessageImpl
import io.github.yusaka39.easySlackbot.slack.UserImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HandlerPackTest {
    @Test
    fun gettingMessageWorksCorrectly() {
        val testMessage = MessageImpl(
            UserImpl("Uabcdefg", "awesomekotlin", "John Doe"),
            "kotlin is awesome.",
            ChannelImpl("Cabcdefg", "secret_group"),
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