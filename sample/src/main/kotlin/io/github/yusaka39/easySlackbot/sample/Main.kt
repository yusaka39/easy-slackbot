package io.github.yusaka39.easySlackbot.sample

import io.github.yusaka39.easySlackbot.annotations.GroupParam
import io.github.yusaka39.easySlackbot.annotations.HandlerFunction
import io.github.yusaka39.easySlackbot.bot.Bot
import io.github.yusaka39.easySlackbot.router.HandlerPack
import io.github.yusaka39.easySlackbot.router.actions.PostAction
import io.github.yusaka39.easySlackbot.router.actions.PutReactionAction

fun main(args: Array<String>) {
    Bot(args[0], "io.github.yusaka39.easySlackbot.sample").run()
}

class Handlers : HandlerPack() {
    @HandlerFunction("""^say\s+(.*)""")
    fun say(@GroupParam(1) words: String) =
        PostAction(this.receivedMessage.channel, "${this.receivedMessage.user.replyString}: $words")

    @HandlerFunction("is released", regexOption = [RegexOption.IGNORE_CASE])
    fun congrats() =
        PutReactionAction(this.receivedMessage, "tada") compose PostAction(this.receivedMessage.channel, "Congrats!")
}