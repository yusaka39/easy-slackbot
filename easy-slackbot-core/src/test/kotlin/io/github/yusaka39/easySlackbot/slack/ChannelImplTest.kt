package io.github.yusaka39.easySlackbot.slack

import kotlin.test.Test
import kotlin.test.assertEquals

class ChannelImplTest {
    private val testChannel = ChannelImpl("Cabcdefg", "secret_group")

    @Test
    fun channelReturnsCorrectId() {
        assertEquals("Cabcdefg", this.testChannel.id)
    }

    @Test
    fun channelReturnsCorrectName() {
        assertEquals("secret_group", this.testChannel.name)
    }
}