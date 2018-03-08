package io.github.yusaka39.easySlackbot.scheduler

import io.github.yusaka39.easySlackbot.router.HandlerPack
import io.github.yusaka39.easySlackbot.router.actions.Action
import io.github.yusaka39.easySlackbot.router.actions.PostAction
import io.github.yusaka39.easySlackbot.slack.Attachment
import io.github.yusaka39.easySlackbot.slack.Message
import io.github.yusaka39.easySlackbot.slack.Slack
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SchedulerServiceImplTest {

    private val latch = CountDownLatch(1)

    private class TestTasks : HandlerPack() {
        fun runSendTo(): Action = PostAction("channelId", "text")
    }

    private val testTaskFactory = object : ScheduledTaskSetFactory {
        override fun create(): Set<ScheduledTask> {
            return setOf(
                ScheduledTask(
                    TestTasks::class, TestTasks::runSendTo,
                    Schedule(0, 0, "UTC", 10, TimeUnit.MINUTES) { 1000L }
                )
            )
        }
    }

    @Test
    fun startWorksFine() {
        var isCalled = false
        val slack = object : Slack {
            override fun sendTo(channelId: String, text: String) {
                isCalled = true
                assertEquals("channelId", channelId)
                assertEquals("text", text)
                this@SchedulerServiceImplTest.latch.countDown()
            }

            override fun putAttachmentTo(channelId: String, vararg attachments: Attachment) {}

            override fun putReactionTo(channelId: String, timestamp: String, emoticonName: String) {}

            override fun sendDirectMessageTo(username: String, text: String) {}

            override fun onReceiveMessage(handler: (message: Message, slack: Slack) -> Unit) {}

            override fun onReceiveDirectMessage(handler: (message: Message, slack: Slack) -> Unit) {}

            override fun onReceiveRepliedMessage(handler: (message: Message, slack: Slack) -> Unit) {}

            override fun getChannelIdOrNullByName(channelName: String): String? = null

            override fun getDmChannelIdOrNullByUserName(username: String): String? = null

            override fun startService() {}

            override fun stopService() {}
        }

        val service = SchedulerServiceImpl(this.testTaskFactory)
        service.start(slack)
        this.latch.await()
        service.stop()
        assertTrue(isCalled)
    }
}