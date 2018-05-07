package io.github.yusaka39.easySlackbot.test.router.actions

import io.github.yusaka39.easySlackbot.api.entity.Action
import io.github.yusaka39.easySlackbot.api.entity.Slack
import io.github.yusaka39.easySlackbot.test.NotImplementedSlack
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