package io.github.yusaka39.easySlackbot.bot

import io.github.yusaka39.easySlackbot.router.AnnotationBasedMessageRouterFactory
import io.github.yusaka39.easySlackbot.scheduler.SchedulerService
import io.github.yusaka39.easySlackbot.scheduler.SchedulerServiceFactory
import io.github.yusaka39.easySlackbot.scheduler.SchedulerServiceImpl
import io.github.yusaka39.easySlackbot.slack.Attachment
import io.github.yusaka39.easySlackbot.slack.Channel
import io.github.yusaka39.easySlackbot.slack.Message
import io.github.yusaka39.easySlackbot.slack.Slack
import io.github.yusaka39.easySlackbot.slack.SlackFactory
import io.github.yusaka39.easySlackbot.slack.User
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BotTest {
    private class TestSlackFactory(
        private val sendHook: (String, String) -> Unit = { _, _ -> },
        private val startHook: () -> Unit = {},
        private val stopHook: () -> Unit = {}
    ) : SlackFactory {
        var onReceiveMessage: (message: Message, slack: Slack) -> Unit = { _, _ -> }
            private set
        var onReceiveDirectMessage: (message: Message, slack: Slack) -> Unit = { _, _ -> }
            private set
        var onReceiveRepliedMessage: (message: Message, slack: Slack) -> Unit = { _, _ -> }
            private set
        val instance = object : Slack {
            override fun sendTo(channelId: String, text: String) {
                this@TestSlackFactory.sendHook(channelId, text)
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
                this@TestSlackFactory.onReceiveMessage = handler
            }

            override fun onReceiveDirectMessage(handler: (message: Message, slack: Slack) -> Unit) {
                this@TestSlackFactory.onReceiveDirectMessage = handler
            }

            override fun onReceiveRepliedMessage(handler: (message: Message, slack: Slack) -> Unit) {
                this@TestSlackFactory.onReceiveRepliedMessage = handler
            }

            override fun getChannelIdOrNullByName(channelName: String): String? {
                TODO("not implemented")
            }

            override fun getDmChannelIdOrNullByUserName(username: String): String? {
                TODO("not implemented")
            }

            override fun startService() {
                this@TestSlackFactory.startHook()
            }

            override fun stopService() {
                this@TestSlackFactory.stopHook()
            }
        }

        override fun create(slackToken: String): Slack = this.instance

    }

    private val testMessageRouterFactory =
        AnnotationBasedMessageRouterFactory("io.github.yusaka39.easySlackbot.bot.testHandlerPack")

    private val testSchedulerServiceFactory = object : SchedulerServiceFactory {
        override fun create(): SchedulerService {
            return object : SchedulerService {
                override fun start(slack: Slack) {
                }

                override fun stop() {
                }
            }
        }
    }


    @Test
    fun startServiceCorrectly() {
        var isStartCalled = false
        val bot = Bot(
            "token",
            this.testMessageRouterFactory,
            this.testSchedulerServiceFactory,
            TestSlackFactory(startHook = { isStartCalled = true }))
        bot.run()
        assertTrue(isStartCalled)
    }

    @Test
    fun stopServiceCorrectly() {
        var isStopCalled = false
        val bot = Bot(
            "token",
            this.testMessageRouterFactory,
            this.testSchedulerServiceFactory,
            TestSlackFactory(stopHook = { isStopCalled = true })
        )
        bot.kill()
        assertTrue(isStopCalled)
    }

    @Test
    fun botHandleMessageCorrectly() {
        var isHookCalled = false
        val slackFactory = TestSlackFactory(sendHook = { channelId, text ->
            isHookCalled = true
            assertEquals("channelId", channelId)
            assertEquals("text", text)
        })
        val slack = slackFactory.instance
        val testMessage = Message(User("foo", "bar", "baz"), "foo", Channel("foo", "bar"), "1234")

        Bot("token", this.testMessageRouterFactory, this.testSchedulerServiceFactory, slackFactory).run {
            slackFactory.onReceiveMessage(testMessage, slack)
            assertTrue(isHookCalled)

            isHookCalled = false
            slackFactory.onReceiveRepliedMessage(testMessage, slack)
            assertTrue(isHookCalled)

            isHookCalled = false
            slackFactory.onReceiveDirectMessage(testMessage, slack)
            assertTrue(isHookCalled)

            isHookCalled = false
            slackFactory.onReceiveMessage(testMessage.copy(text = "bar"), slack)
            assertFalse(isHookCalled)
        }
    }
}