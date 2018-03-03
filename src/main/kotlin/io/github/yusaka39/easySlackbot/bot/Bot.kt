package io.github.yusaka39.easySlackbot.bot

import io.github.yusaka39.easySlackbot.lib.Log
import io.github.yusaka39.easySlackbot.router.AnnotationBasedMessageRouterFactory
import io.github.yusaka39.easySlackbot.router.HandlerType
import io.github.yusaka39.easySlackbot.router.MessageRouterFactory
import io.github.yusaka39.easySlackbot.slack.Message
import io.github.yusaka39.easySlackbot.slack.SlackFactory
import io.github.yusaka39.easySlackbot.slack.impl.SlackletSlackFactory


class Bot internal constructor(
    slackToken: String,
    messageRouterFactory: MessageRouterFactory,
    slackFactory: SlackFactory
) {
    constructor(slackToken: String, searchPackage: String) :
            this(slackToken, AnnotationBasedMessageRouterFactory(searchPackage), SlackletSlackFactory())

    private val messageRouter = messageRouterFactory.create()
    private val slack = slackFactory.create(slackToken).apply {
        fun runActionForMessage(message: Message, type: HandlerType) {
            try {
                val handler = this@Bot.messageRouter.findHandlerFor(message, type) ?: return
                handler.generateActionForMessage(message).run(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        this.onReceiveMessage { message, _ ->
            runActionForMessage(message, HandlerType.ListenTo)
        }
        this.onReceiveRepliedMessage { message, _ ->
            runActionForMessage(message, HandlerType.RespondTo)
        }
        this.onReceiveDirectMessage { message, _ ->
            runActionForMessage(message, HandlerType.RespondTo)
        }
    }

    fun run() {
        this.slack.startService()
        Log.i("Bot service is started")
    }

    fun kill() {
        this.slack.stopService()
        Log.i("Bot service is killed")
    }
}
