package io.github.yusaka39.easySlackbot.sample

import com.github.yusaka39.slackbot.api.entity.Action
import com.github.yusaka39.slackbot.api.entity.Message
import com.github.yusaka39.slackbot.api.handler.Handler
import com.github.yusaka39.slackbot.api.handler.HandlerPack
import com.github.yusaka39.slackbot.api.handler.HandlerPackHandler
import com.github.yusaka39.slackbot.api.handler.HandlerProvider
import com.github.yusaka39.slackbot.core.annotations.RunWithInterval
import com.github.yusaka39.slackbot.core.bot.BotBuilder
import com.github.yusaka39.slackbot.stdplugin.actions.PostAction
import com.github.yusaka39.slackbot.stdplugin.actions.PostWithChannelNameAction
import com.github.yusaka39.slackbot.stdplugin.actions.PutReactionAction
import com.github.yusaka39.slackbot.stdplugin.actions.putAttachmentToChannelAction
import com.github.yusaka39.slackbot.stdplugin.handler.HandlerFunctionHandlerProvider
import com.github.yusaka39.slackbot.stdplugin.handler.annotations.GroupParam
import com.github.yusaka39.slackbot.stdplugin.handler.annotations.HandlerFunction
import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

fun main(args: Array<String>) {
    BotBuilder {
        setToken(args[0])
        setHandlerPackage("io.github.yusaka39.easySlackbot.sample")
        addFeature(HandlerFunctionHandlerProvider())
    }.build().run()
}

annotation class Hoge

class AttachmentHandlerProvider : HandlerProvider {
    override fun createHandlerOrNull(annotations: List<Annotation>, kClass: KClass<*>, callable: KCallable<Action>): Handler? {
        val anno = annotations.firstOrNull { it is Hoge } ?: return null

        return object : HandlerPackHandler() {
            override val kCallable: KCallable<*>
                get() = callable
            override val kClass: KClass<*>
                get() = kClass

            override fun getArgumentsFromTargetMessage(message: Message): Array<Any?> {
                return emptyArray()
            }

            override fun isMatchedTo(message: Message, isReplyMessage: Boolean): Boolean {
                return message.attachments.isNotEmpty()
            }

        }
    }
}

class Handlers : HandlerPack() {
    @HandlerFunction("""^say\s+(.*)""")
    fun say(@GroupParam(1) words: String) =
        PostAction(this.receivedMessage.channel, "${this.receivedMessage.user.replyString}: $words")

    @HandlerFunction("is released", regexOption = [RegexOption.IGNORE_CASE])
    fun congrats() =
        PutReactionAction(this.receivedMessage, "tada") compose PostAction(this.receivedMessage.channel, "Congrats!")

    @HandlerFunction("""^plus\s+(\d+)\s+(\d+)""")
    fun plus(@GroupParam(1) a: Int, @GroupParam(2) b: Int) = PostAction(this.receivedMessage.channel, "${ a + b }")

    @HandlerFunction("^status$")
    fun showStatus() = putAttachmentToChannelAction(this.receivedMessage.channel) {
        attachment {
            color = "#00AA99"
            title = "Bot Status"
            field {
                title = "Status"
                value = "I'm fine"
                isShort = true
            }
            field {
                title = "Uptime"
                value = ManagementFactory.getRuntimeMXBean().uptime.toString()
                isShort = true
            }
        }
        attachment {
            color = "#DD3333"
            title = "Hanger"
            field {
                title = "Stomach"
                value = "Stomach is grumbling"
                isShort = false
            }
        }
    }

    @Hoge
    fun hoge() = PostAction(this.receivedMessage.channel, "attach!")
}