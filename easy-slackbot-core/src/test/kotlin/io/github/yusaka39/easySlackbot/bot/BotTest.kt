package io.github.yusaka39.easySlackbot.bot

import io.github.yusaka39.easySlackbot.NotImplementedSlack
import io.github.yusaka39.easySlackbot.router.AnnotationBasedMessageRouterFactory
import io.github.yusaka39.easySlackbot.scheduler.SchedulerService
import io.github.yusaka39.easySlackbot.scheduler.SchedulerServiceFactory
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
        val instance = object : NotImplementedSlack() {
            override fun sendTo(channelId: String, text: String) {
                this@TestSlackFactory.sendHook(channelId, text)
            }

            override fun onReceiveMessage(handler: (message: Message, slack: Slack) -> Unit) {
                this@TestSlackFactory.onReceiveMessage = handler
            }

            override fun onReceiveDirectMessage(handler: (message: Message, slack: Slack) -> Unit) {
                this@TestSlackFactory.onReceiveDirectMessage = handler
            }

            override fun onReceiveReply(handler: (message: Message, slack: Slack) -> Unit) {
                this@TestSlackFactory.onReceiveRepliedMessage = handler
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

    private class TestSchedulerFactory(
        val startHook: () -> Unit = {},
        val stopHook: () -> Unit = {}
    ) : SchedulerServiceFactory {
        override fun create(): SchedulerService = object : SchedulerService {
            override fun start(slack: Slack) {
                this@TestSchedulerFactory.startHook()
            }

            override fun stop() {
                this@TestSchedulerFactory.stopHook()
            }
        }
    }


    @Test
    fun startServiceCorrectly() {
        var isStartCalled = false
        var isSchedulerStarted = false
        val bot = Bot(
            "token",
            this.testMessageRouterFactory,
            TestSchedulerFactory(startHook = { isSchedulerStarted = true }),
            TestSlackFactory(startHook = { isStartCalled = true })
        )
        bot.run()
        assertTrue(isStartCalled)
        assertTrue(isSchedulerStarted)
    }

    @Test
    fun stopServiceCorrectly() {
        var isStopCalled = false
        var isSchedulerStopped = false
        val bot = Bot(
            "token",
            this.testMessageRouterFactory,
            TestSchedulerFactory(stopHook = { isSchedulerStopped = true }),
            TestSlackFactory(stopHook = { isStopCalled = true })
        )
        bot.kill()
        assertTrue(isStopCalled)
        assertTrue(isSchedulerStopped)
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

        Bot("token", this.testMessageRouterFactory, TestSchedulerFactory(), slackFactory).run {
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