package io.github.yusaka39.easySlackbot.router.actions

import io.github.yusaka39.easySlackbot.slack.Channel
import io.github.yusaka39.easySlackbot.slack.Slack

class PostAction(val channel: Channel, val text: String) : Action {
    override fun run(slack: Slack) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}