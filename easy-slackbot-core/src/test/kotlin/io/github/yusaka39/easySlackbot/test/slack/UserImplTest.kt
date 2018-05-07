package io.github.yusaka39.easySlackbot.test.slack

import io.github.yusaka39.easySlackbot.slack.UserImpl
import kotlin.test.Test
import kotlin.test.assertEquals

class UserImplTest {
    private val testUser = UserImpl("Uabcdefg", "awesomekotlin", "John Doe")
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