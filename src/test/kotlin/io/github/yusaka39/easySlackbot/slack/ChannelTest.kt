package io.github.yusaka39.easySlackbot.slack

import org.junit.Test
import kotlin.test.assertEquals

class ChannelTest {
    private val testChannel = Channel("Cabcdefg", "secret_group")

    @Test
    fun channelReturnsCorrectId() {
        assertEquals("Cabcdefg", this.testChannel.id)
    }

    @Test
    fun channelReturnsCorrectName() {
        assertEquals("secret_group", this.testChannel.name)
    }
}