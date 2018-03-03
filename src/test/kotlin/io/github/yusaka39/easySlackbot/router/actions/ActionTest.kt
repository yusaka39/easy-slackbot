package io.github.yusaka39.easySlackbot.router.actions

import io.github.yusaka39.easySlackbot.slack.Attachment
import io.github.yusaka39.easySlackbot.slack.Message
import io.github.yusaka39.easySlackbot.slack.Slack
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ActionTest {
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

        (first compose second).run(NopSlack())
        assertTrue(isFirstCalled)
        assertTrue(isSecondCalled)
    }
}