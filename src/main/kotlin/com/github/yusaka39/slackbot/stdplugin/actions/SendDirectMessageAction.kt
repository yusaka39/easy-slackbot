package com.github.yusaka39.slackbot.stdplugin.actions

import com.github.yusaka39.slackbot.api.entity.Action
import com.github.yusaka39.slackbot.api.entity.Slack
import com.github.yusaka39.slackbot.api.entity.User

class SendDirectMessageAction(private val to: String, private val text: String) : Action {
    constructor(toUser: User, text: String) : this(toUser.userName, text)

    override fun run(slack: Slack) {
        slack.sendDirectMessageTo(to, text)
    }
}