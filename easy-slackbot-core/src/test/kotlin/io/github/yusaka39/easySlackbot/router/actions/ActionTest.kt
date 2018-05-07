package io.github.yusaka39.easySlackbot.router.actions

import io.github.yusaka39.easySlackbot.NotImplementedSlack
import io.github.yusaka39.easySlackbot.slack.Attachment
import io.github.yusaka39.easySlackbot.slack.Message
import io.github.yusaka39.easySlackbot.slack.Slack
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ActionTest {
    @Test
    fun composeWorksCorrectly() {
        var isFirstCalled = false
        var isSecondCalled = false
        val first = object : Action {
            override fun run(slack: Slack) {
                assertFalse(isFirstCalled)
                assertFalse(isSecondCalled)
                isFirstCalled = true
            }
        }
        val second = object : Action {
            override fun run(slack: Slack) {
                assertTrue(isFirstCalled)
                assertFalse(isSecondCalled)
                isSecondCalled = true
            }
        }

        (first compose second).run(NotImplementedSlack())
        assertTrue(isFirstCalled)
        assertTrue(isSecondCalled)
    }
}