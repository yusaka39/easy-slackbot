package io.github.yusaka39.easySlackbot.router.actions

import io.github.yusaka39.easySlackbot.NotImplementedSlack
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
    private class PutAttachmentHookSlack(
            private val hook: (String, Array<out Attachment>) -> Unit
    ) : NotImplementedSlack() {
        override fun putAttachmentTo(channelId: String, vararg attachments: Attachment) =
            this.hook(channelId, attachments)

        override fun getDmChannelIdOrNullByUserName(username: String): String? {
            if (username == "john") {
                return "C123456"
            }
            return null
        }
    }

    private class ReturnNullAsDmChannelIdSlack : NotImplementedSlack() {
        override fun getDmChannelIdOrNullByUserName(username: String): String? = null
    }

    private val testAttachment = AttachmentBuilder {
        text = "text"
        title = "title"
    }.build()

    private val testAttachment2 = AttachmentBuilder {
        text = "text2"
        title = "title2"
    }.build()

    @Test
    fun builderWithUserWorksFine() {
        var isHookCalled = false
        val hook = PutAttachmentHookSlack { channelId, attachments ->
            isHookCalled = true
            assertEquals("C123456", channelId)
            assertEquals(listOf(this.testAttachment, this.testAttachment2), attachments.toList())
        }

        putAttachmentToUserAction("john") {
            attachment {
                text = "text"
                title = "title"
            }
            attachment {
                text = "text2"
                title = "title2"
            }
        }.run(hook)
        assertTrue(isHookCalled)

        putAttachmentToUserAction(User("foo", "john", "bar")) {
            attachment {
                text = "text"
                title = "title"
            }
            attachment {
                text = "text2"
                title = "title2"
            }
        }.run(hook)
        assertTrue(isHookCalled)
    }

    @Test
    fun builderWithChannelWorksFine() {
        var isHookCalled = false
        val hook = PutAttachmentHookSlack { channelId, attachments ->
            isHookCalled = true
            assertEquals("C123456", channelId)
            assertEquals(listOf(this.testAttachment, this.testAttachment2), attachments.toList())
        }

        putAttachmentToChannelAction("C123456") {
            attachment {
                text = "text"
                title = "title"
            }
            attachment {
                text = "text2"
                title = "title2"
            }
        }.run(hook)
        assertTrue(isHookCalled)

        putAttachmentToChannelAction(Channel("C123456", "foo")) {
            attachment {
                text = "text"
                title = "title"
            }
            attachment {
                text = "text2"
                title = "title2"
            }
        }.run(hook)
        assertTrue(isHookCalled)
    }

    @Test
    fun putAttachmentActionFailsCorrectly() {
        assertFailsWith<IllegalStateException> {
            PutAttachmentAction(null, null, listOf(this.testAttachment))
                    .run(ReturnNullAsDmChannelIdSlack())
        }

        assertFailsWith<IllegalStateException> {
            putAttachmentToUserAction("jane") {
                attachment {
                    text = "text"
                    title = "title"
                }
            }.run(ReturnNullAsDmChannelIdSlack())
        }
    }
}