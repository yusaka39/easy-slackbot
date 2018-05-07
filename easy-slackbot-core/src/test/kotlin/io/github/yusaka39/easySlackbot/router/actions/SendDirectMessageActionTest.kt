package io.github.yusaka39.easySlackbot.router.actions

import io.github.yusaka39.easySlackbot.NotImplementedSlack
import io.github.yusaka39.easySlackbot.slack.UserImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SendDirectMessageActionTest {
    private class DirectMessageHookSlack(
            private val hook: (String, String) -> Unit
    ) : NotImplementedSlack() {
        override fun sendDirectMessageTo(username: String, text: String) = this.hook(username, text)
    }

    @Test
    fun sendDirectMessageActionWorksFine() {
        var isHookCalled = false
        val hook = DirectMessageHookSlack { userName, text ->
            isHookCalled = true
            assertEquals("john", userName)
            assertEquals("This is text", text)
        }

        SendDirectMessageAction("john", "This is text").run(hook)
        assertTrue(isHookCalled)

        isHookCalled = false
        SendDirectMessageAction(UserImpl("john", "john", "John Doe"), "This is text").run(hook)
        assertTrue(isHookCalled)
    }
}