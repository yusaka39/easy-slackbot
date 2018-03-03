package io.github.yusaka39.easySlackbot.slack

import org.junit.Test
import kotlin.test.assertEquals

class MessageTest {
    private val johnDoe = User("Uabcdefg", "awesomekotlin", "John Doe")
    private val secretGroup = Channel("Cabcdefg", "secret_group")
    private val testMessage = Message(
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