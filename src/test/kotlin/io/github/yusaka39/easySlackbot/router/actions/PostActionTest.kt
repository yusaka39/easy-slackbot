package io.github.yusaka39.easySlackbot.router.actions

import io.github.yusaka39.easySlackbot.NotImplementedSlack
import io.github.yusaka39.easySlackbot.slack.Attachment
import io.github.yusaka39.easySlackbot.slack.Channel
import io.github.yusaka39.easySlackbot.slack.Message
import io.github.yusaka39.easySlackbot.slack.Slack
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
        PostAction(Channel("Cabcdefg", "abcdefg"), "foobar").run(hook)
        assertTrue(isHookCalled)
    }
}