package io.github.yusaka39.easySlackbot.slack

import org.junit.Test
import kotlin.test.assertEquals

class UserTest {
    private val testUser = User("Uabcdefg", "awesomekotlin", "John Doe")
    @Test
    fun userReturnsCorrectReplyString() {
        assertEquals("<@${this.testUser.id}>", this.testUser.replyString)
    }

    @Test
    fun userReturnsCorrectUserName() {
        assertEquals("awesomekotlin", this.testUser.userName)
    }

    @Test
    fun userReturnsCorrectRealName() {
        assertEquals("John Doe", this.testUser.realName)
    }

    @Test
    fun userReturnsCorrectId() {
        assertEquals("Uabcdefg", this.testUser.id)
    }
}