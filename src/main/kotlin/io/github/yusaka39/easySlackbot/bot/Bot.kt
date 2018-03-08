package io.github.yusaka39.easySlackbot.bot

import io.github.yusaka39.easySlackbot.lib.logger
import io.github.yusaka39.easySlackbot.router.AnnotationBasedMessageRouterFactory
import io.github.yusaka39.easySlackbot.router.HandlerType
import io.github.yusaka39.easySlackbot.router.MessageRouterFactory
import io.github.yusaka39.easySlackbot.scheduler.AnnotationBasedSchedulerServiceFactory
import io.github.yusaka39.easySlackbot.scheduler.SchedulerService
import io.github.yusaka39.easySlackbot.scheduler.SchedulerServiceFactory
import io.github.yusaka39.easySlackbot.slack.Message
import io.github.yusaka39.easySlackbot.slack.SlackFactory
import io.github.yusaka39.easySlackbot.slack.impl.SimpleSlackApiSlackFactory


class Bot internal constructor(
    slackToken: String,
    messageRouterFactory: MessageRouterFactory,
    schedulerServiceFactory: SchedulerServiceFactory,
    slackFactory: SlackFactory
) {
    constructor(slackToken: String, searchPackage: String) : this(
        slackToken,
        AnnotationBasedMessageRouterFactory(searchPackage),
        AnnotationBasedSchedulerServiceFactory(searchPackage),
        SimpleSlackApiSlackFactory()
    )

    private val logger by this.logger()

    private val messageRouter = messageRouterFactory.create()
    private val scheduler: SchedulerService = schedulerServiceFactory.create()
    private val slack = slackFactory.create(slackToken).apply {
        fun runActionForMessage(message: Message, type: HandlerType) {
            try {
                val handler = this@Bot.messageRouter.findHandlerFor(message, type) ?: return
                handler.generateActionForMessage(message).run(this)
            } catch (e: Exception) {
                this@Bot.logger.warn("Exception is caused while executing handler for message: \"${message.text}\".", e)
            }
        }
        this.onReceiveMessage { message, _ ->
            this@Bot.logger.debug("Received message \"${message.text}\".")
            runActionForMessage(message, HandlerType.ListenTo)
        }
        this.onReceiveRepliedMessage { message, _ ->
            this@Bot.logger.debug("Received message \"${message.text}\".")
            runActionForMessage(message, HandlerType.RespondTo)
        }
        this.onReceiveDirectMessage { message, _ ->
            this@Bot.logger.debug("Received message \"${message.text}\".")
            runActionForMessage(message, HandlerType.RespondTo)
        }
    }

    fun run() {
        this.slack.startService()
        this.scheduler.start(this.slack)
        this.logger.info("Bot service is started.")
    }

    fun kill() {
        this.scheduler.stop()
        this.slack.stopService()
        this.logger.info("Bot service is killed.")
    }
}
