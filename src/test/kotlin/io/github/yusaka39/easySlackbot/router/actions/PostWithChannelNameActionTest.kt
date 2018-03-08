package io.github.yusaka39.easySlackbot.router.actions

import io.github.yusaka39.easySlackbot.slack.Attachment
import io.github.yusaka39.easySlackbot.slack.Message
import io.github.yusaka39.easySlackbot.slack.Slack
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PostWithChannelNameActionTest {
    @Test
    fun postActionWorksFine() {
        var isCalled = false
        val slack = object : Slack {
            override fun sendTo(channelId: String, text: String) {
                assertEquals("channelId", channelId)
                assertEquals("text", text)
                isCalled = true
            }

            override fun getChannelIdOrNullByName(channelName: String): String? {
                return if (channelName == "channelName") {
                    return "channelId"
                } else {
                    null
                }
            }

            override fun putAttachmentTo(channelId: String, vararg attachment: Attachment) {}
            override fun putReactionTo(channelId: String, timestamp: String, emoticonName: String) {}
            override fun sendDirectMessageTo(username: String, text: String) {}
            override fun onReceiveMessage(handler: (message: Message, slack: Slack) -> Unit) {}
            override fun onReceiveDirectMessage(handler: (message: Message, slack: Slack) -> Unit) {}
            override fun onReceiveRepliedMessage(handler: (message: Message, slack: Slack) -> Unit) {}
            override fun getDmChannelIdOrNullByUserName(username: String): String? = null
            override fun startService() {}
            override fun stopService() {}

        }
        PostWithChannelNameAction("channelName", "text").run(slack)
        assertTrue(isCalled)

        assertFailsWith<IllegalStateException> {
            PostWithChannelNameAction("channelName2", "text").run(slack)
        }
    }
}