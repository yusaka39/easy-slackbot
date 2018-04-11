package io.github.yusaka39.easySlackbot.router.actions

import io.github.yusaka39.easySlackbot.NopSlack
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
        val slack = object : NopSlack() {

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
        }
        PostWithChannelNameAction("channelName", "text").run(slack)
        assertTrue(isCalled)

        assertFailsWith<IllegalStateException> {
            PostWithChannelNameAction("channelName2", "text").run(slack)
        }
    }
}