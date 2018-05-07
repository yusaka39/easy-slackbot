package io.github.yusaka39.easySlackbot.router.actions

import io.github.yusaka39.easySlackbot.NotImplementedSlack
import io.github.yusaka39.easySlackbot.slack.ChannelImpl
import io.github.yusaka39.easySlackbot.slack.MessageImpl
import io.github.yusaka39.easySlackbot.slack.UserImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PutReactionActionTest {
    private class PutReactionHookSlack(
            private val hook: (String, String, String) -> Unit
    ) : NotImplementedSlack() {
        override fun putReactionTo(channelId: String, timestamp: String, emoticonName: String) =
            this.hook(channelId, timestamp, emoticonName)
    }

    @Test
    fun putReactionActionWorksFine() {
        var isHookCalled = false
        val hook = PutReactionHookSlack { channelId, timestamp, emoticonName ->
            isHookCalled = true
            assertEquals("Cabcdefg", channelId)
            assertEquals("12345678.123456", timestamp)
            assertEquals("tada", emoticonName)
        }

        PutReactionAction("Cabcdefg", "12345678.123456", "tada").run(hook)
        assertTrue(isHookCalled)

        isHookCalled = false
        PutReactionAction(
            MessageImpl(UserImpl("foo", "bar", "foobar"), "text", ChannelImpl("Cabcdefg", "channelName"), "12345678.123456"),
            "tada"
        ).run(hook)
        assertTrue(isHookCalled)
    }
}