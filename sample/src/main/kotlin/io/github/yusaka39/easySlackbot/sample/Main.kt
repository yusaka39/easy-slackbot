package io.github.yusaka39.easySlackbot.sample

import io.github.yusaka39.easySlackbot.annotations.GroupParam
import io.github.yusaka39.easySlackbot.annotations.HandlerFunction
import io.github.yusaka39.easySlackbot.annotations.RunWithInterval
import io.github.yusaka39.easySlackbot.bot.Bot
import io.github.yusaka39.easySlackbot.router.HandlerPack
import io.github.yusaka39.easySlackbot.router.actions.PostAction
import io.github.yusaka39.easySlackbot.router.actions.PostWithChannelNameAction
import io.github.yusaka39.easySlackbot.router.actions.PutReactionAction
import io.github.yusaka39.easySlackbot.router.actions.putAttachmentToChannelAction
import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    Bot(args[0], "io.github.yusaka39.easySlackbot.sample").run()
}

class Handlers : HandlerPack() {
    @HandlerFunction("""^say\s+(.*)""")
    fun say(@GroupParam(1) words: String) =
        PostAction(this.receivedMessage.channel, "${this.receivedMessage.user.replyString}: $words")

    @HandlerFunction("is released", regexOption = [RegexOption.IGNORE_CASE])
    fun congrats() =
        PutReactionAction(
            this.receivedMessage,
            "tada"
        ) compose PostAction(this.receivedMessage.channel, "Congrats!")

    @HandlerFunction("""^plus\s+(\d+)\s+(\d+)""")
    fun plus(@GroupParam(1) a: Int, @GroupParam(2) b: Int) =
        PostAction(this.receivedMessage.channel, "${a + b}")

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

    @get:RunWithInterval(0, 0, "UTC", 24, TimeUnit.HOURS)
    val takeANap =
        PostWithChannelNameAction("general", "Are you still working? Would you like to take a nap?")
}
