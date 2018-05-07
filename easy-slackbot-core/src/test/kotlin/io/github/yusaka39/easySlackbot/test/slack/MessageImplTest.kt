package io.github.yusaka39.easySlackbot.test.slack

import io.github.yusaka39.easySlackbot.slack.ChannelImpl
import io.github.yusaka39.easySlackbot.slack.MessageImpl
import io.github.yusaka39.easySlackbot.slack.UserImpl
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageImplTest {
    private val johnDoe = UserImpl("Uabcdefg", "awesomekotlin", "John Doe")
    private val secretGroup = ChannelImpl("Cabcdefg", "secret_group")
    private val testMessage = MessageImpl(
            this.johnDoe,
            "kotlin is awesome.",
            this.secretGroup,
            "12345678.123456"
    )

    @Test
    fun messageReturnsCorrectUser() {
        assertEquals(this.johnDoe, this.testMessage.user)
    }

    @Test
    fun messageReturnsCorrectText() {
        assertEquals("kotlin is awesome.", this.testMessage.text)
    }

    @Test
    fun messageReturnsCorrectChannel() {
        assertEquals(this.secretGroup, this.testMessage.channel)
    }

    @Test
    fun messageReturnsCorrectTimestamp() {
        assertEquals("12345678.123456", this.testMessage.timestamp)
    }
}