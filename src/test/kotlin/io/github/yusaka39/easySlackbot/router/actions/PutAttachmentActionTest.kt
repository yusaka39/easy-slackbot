package io.github.yusaka39.easySlackbot.router.actions

import io.github.yusaka39.easySlackbot.slack.Attachment
import io.github.yusaka39.easySlackbot.slack.AttachmentBuilder
import io.github.yusaka39.easySlackbot.slack.Channel
import io.github.yusaka39.easySlackbot.slack.Message
import io.github.yusaka39.easySlackbot.slack.Slack
import io.github.yusaka39.easySlackbot.slack.User
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PutAttachmentActionTest {
    private class PutAttachmentHookSlack(private val hook: (String, Attachment) -> Unit) : Slack {
        override fun sendTo(channelId: String, text: String) {
            TODO("not implemented")
        }

        override fun putAttachmentTo(channelId: String, attachment: Attachment) =
            this.hook(channelId, attachment)

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
            if (username == "john") {
                return "C123456"
            }
            return null
        }

        override fun startService() {
            TODO("not implemented")
        }

        override fun stopService() {
            TODO("not implemented")
        }

    }

    private class NopSlack : Slack {
        override fun sendTo(channelId: String, text: String) {
            TODO("not implemented")
        }

        override fun putAttachmentTo(channelId: String, attachment: Attachment) {
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
            return null
        }

        override fun startService() {
            TODO("not implemented")
        }

        override fun stopService() {
            TODO("not implemented")
        }

    }

    private val testAttachment = AttachmentBuilder {
        text = "text"
        title = "title"
    }.build()

    @Test
    fun builderWithUserWorksFine() {
        var isHookCalled = false
        val hook = PutAttachmentHookSlack { channelId, attachment ->
            isHookCalled = true
            assertEquals("C123456", channelId)
            assertEquals(testAttachment, attachment)
        }

        putAttachmentToUserAction("john") {
            text = "text"
            title = "title"
        }.run(hook)
        assertTrue(isHookCalled)

        putAttachmentToUserAction(User("foo", "john", "bar")) {
            text = "text"
            title = "title"
        }.run(hook)
        assertTrue(isHookCalled)
    }

    @Test
    fun builderWithChannelWorksFine() {
        var isHookCalled = false
        val hook = PutAttachmentHookSlack { channelId, attachment ->
            isHookCalled = true
            assertEquals("C123456", channelId)
            assertEquals(testAttachment, attachment)
        }

        putAttachmentToChannelAction("C123456") {
            text = "text"
            title = "title"
        }.run(hook)
        assertTrue(isHookCalled)

        putAttachmentToChannelAction(Channel("C123456", "foo")) {
            text = "text"
            title = "title"
        }.run(hook)
        assertTrue(isHookCalled)
    }

    @Test
    fun putAttachmentActionFailsCorrectly() {
        assertFailsWith<IllegalStateException> {
            PutAttachmentAction(null, null, this.testAttachment).run(NopSlack())
        }

        assertFailsWith<IllegalStateException> {
            putAttachmentToUserAction("jane") {
                text = "text"
                title = "title"
            }.run(NopSlack())
        }
    }
}