package io.github.yusaka39.easySlackbot.router.actions

import io.github.yusaka39.easySlackbot.NotImplementedSlack
import io.github.yusaka39.easySlackbot.slack.Attachment
import io.github.yusaka39.easySlackbot.slack.Channel
import io.github.yusaka39.easySlackbot.slack.Message
import io.github.yusaka39.easySlackbot.slack.Slack
import io.github.yusaka39.easySlackbot.slack.User
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
            Message(User("foo", "bar", "foobar"), "text", Channel("Cabcdefg", "channelName"), "12345678.123456"),
            "tada"
        ).run(hook)
        assertTrue(isHookCalled)
    }
}