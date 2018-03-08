package io.github.yusaka39.easySlackbot.router.actions

import io.github.yusaka39.easySlackbot.slack.Attachment
import io.github.yusaka39.easySlackbot.slack.Message
import io.github.yusaka39.easySlackbot.slack.Slack
import io.github.yusaka39.easySlackbot.slack.User
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SendDirectMessageActionTest {
    private class DirectMessageHookSlack(private val hook: (String, String) -> Unit) : Slack {
        override fun sendTo(channelId: String, text: String) {
            TODO("not implemented")
        }

        override fun putAttachmentTo(channelId: String, vararg attachments: Attachment) {
            TODO("not implemented")
        }

        override fun putReactionTo(channelId: String, timestamp: String, emoticonName: String) {
            TODO("not implemented")
        }

        override fun sendDirectMessageTo(username: String, text: String) = this.hook(username, text)

        override fun onReceiveMessage(handler: (message: Message, slack: Slack) -> Unit) {
            TODO("not implemented")
        }

        override fun onReceiveDirectMessage(handler: (message: Message, slack: Slack) -> Unit) {
            TODO("not implemented")
        }

        override fun onReceiveReply(handler: (message: Message, slack: Slack) -> Unit) {
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
        SendDirectMessageAction(User("john", "john", "John Doe"), "This is text").run(hook)
        assertTrue(isHookCalled)
    }
}