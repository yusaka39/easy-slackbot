package io.github.yusaka39.easySlackbot.test.router.actions

import io.github.yusaka39.easySlackbot.router.actions.PostAction
import io.github.yusaka39.easySlackbot.slack.ChannelImpl
import io.github.yusaka39.easySlackbot.test.NotImplementedSlack
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PostActionTest {
    private class PostHookSlack(
            private val hook: (String, String) -> Unit
    ) : NotImplementedSlack() {
        override fun sendTo(channelId: String, text: String) = this.hook(channelId, text)
    }

    @Test
    fun postActionWorksFine() {
        var isHookCalled = false
        val hook = PostHookSlack { channelId, text ->
            isHookCalled = true
            assertEquals("Cabcdefg", channelId)
            assertEquals("foobar", text)
        }

        PostAction("Cabcdefg", "foobar").run(hook)
        assertTrue(isHookCalled)

        isHookCalled = false
        PostAction(ChannelImpl("Cabcdefg", "abcdefg"), "foobar").run(hook)
        assertTrue(isHookCalled)
    }
}