package io.github.yusaka39.easySlackbot.router.actions

import io.github.yusaka39.easySlackbot.slack.Attachment
import io.github.yusaka39.easySlackbot.slack.Channel
import io.github.yusaka39.easySlackbot.slack.Message
import io.github.yusaka39.easySlackbot.slack.Slack
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PostActionTest {
    private class PostHookSlack(private val hook: (String, String) -> Unit) : Slack {
        override fun sendTo(channelId: String, text: String) = this.hook(channelId, text)

        override fun putAttachmentTo(channelId: String, vararg attachment: Attachment) {
            TODO("not implemented")
        }

        override fun putReactionTo(channelId: String, timestamp: String, emoticonName: String) {
            TODO("not implemented")
        }

        override fun sendDirectMessageTo(username: String, text: String) {
            TODO("not implemented")
        }

        override fun onReceiveMessage(handler: (message: Message, slack: Slack) -> Unit) {
            TODO("not implemented")
        }

        override fun onReceiveDirectMessage(handler: (message: Message, slack: Slack) -> Unit) {
            TODO("not implemented")
        }

        override fun onReceiveRepliedMessage(handler: (message: Message, slack: Slack) -> Unit) {
            TODO("not implemented")
        }

        override fun getChannelIdOrNullByName(channelName: String): String? {
            TODO("not implemented")
        }

        override fun getDmChannelIdOrNullByUserName(username: String): String? {
            TODO("not implemented")
        }

        override fun startService() {
            TODO("not implemented")
        }

        override fun stopService() {
            TODO("not implemented")
        }

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