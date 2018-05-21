package io.github.yusaka39.easySlackbot.bot

import io.github.yusaka39.easySlackbot.api.entity.Message
import io.github.yusaka39.easySlackbot.api.handler.HandlerProvider
import io.github.yusaka39.easySlackbot.lib.logger
import io.github.yusaka39.easySlackbot.router.HandlerSetFactoryImpl
import io.github.yusaka39.easySlackbot.router.MessageRouterFactory
import io.github.yusaka39.easySlackbot.router.MessageRouterFactoryImpl
import io.github.yusaka39.easySlackbot.scheduler.AnnotationBasedSchedulerServiceFactory
import io.github.yusaka39.easySlackbot.scheduler.SchedulerService
import io.github.yusaka39.easySlackbot.scheduler.SchedulerServiceFactory
import io.github.yusaka39.easySlackbot.slack.SlackFactory
import io.github.yusaka39.easySlackbot.slack.impl.SimpleSlackApiSlackFactory


class Bot internal constructor(
        slackToken: String,
        messageRouterFactory: MessageRouterFactory,
        schedulerServiceFactory: SchedulerServiceFactory,
        slackFactory: SlackFactory
) {
    private val logger by this.logger()

    private val messageRouter = messageRouterFactory.create()
    private val scheduler: SchedulerService = schedulerServiceFactory.create()
    private val slack = slackFactory.create(slackToken).apply {
        fun runActionForMessage(message: Message, isRepliedMessage: Boolean) {
            try {
                val handlers = this@Bot.messageRouter.findHandlersFor(message, isRepliedMessage)
                if (handlers.isEmpty()) {
                    return
                }
                handlers.forEach {
                    it.createActionProvider(message)().run(this)
                }
            } catch (e: Exception) {
                this@Bot.logger.warn("Exception is caused while executing handler for message: \"${message.text}\".", e)
            }
        }
        this.onReceiveMessage { message, _ ->
            this@Bot.logger.debug("Received message \"${message.text}\".")
            runActionForMessage(message, false)
        }
        this.onReceiveReply { message, _ ->
            this@Bot.logger.debug("Received message \"${message.text}\".")
            runActionForMessage(message, true)
        }
        this.onReceiveDirectMessage { message, _ ->
            this@Bot.logger.debug("Received message \"${message.text}\".")
            runActionForMessage(message, true)
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

class BotBuilder(initializer: BotBuilder.() -> Unit = {}) {
    private var features = emptyList<HandlerProvider>()
    private var token: String? = null
    private var handlerPackageName: String? = null
    init {
        this.initializer()
    }

    fun addFeature(provider: HandlerProvider): BotBuilder = this.apply {
        this.features += provider
    }

    fun setToken(token: String): BotBuilder = this.apply {
        this.token = token
    }

    fun setHandlerPackage(packageName: String): BotBuilder = this.apply {
        this.handlerPackageName = packageName
    }

    fun build(): Bot {
        val token = this.token ?: throw IllegalStateException("Slack token is not provided")
        val handlerPackageName = this.handlerPackageName ?: throw IllegalStateException(
                "Package name contains handler classes is not provided"
        )
        return Bot(
                token,
                MessageRouterFactoryImpl(HandlerSetFactoryImpl(handlerPackageName, this.features)),
                AnnotationBasedSchedulerServiceFactory(handlerPackageName),
                SimpleSlackApiSlackFactory()
        )
    }
}
