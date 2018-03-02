package io.github.yusaka39.easySlackbot.router.actions

import io.github.yusaka39.easySlackbot.slack.Slack
import io.github.yusaka39.easySlackbot.slack.User

class SendDirectMessageAction(private val to: User, private val text: String) : Action {
    override fun run(slack: Slack) {
        slack.sendDirectMessageTo(to, text)
    }
}