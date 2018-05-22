package com.github.yusaka39.slackbot.stdplugin.actions

import com.github.yusaka39.slackbot.api.entity.Action
import com.github.yusaka39.slackbot.api.entity.Slack
import kotlin.concurrent.thread

class FutureAction private constructor(private val procedure: (Slack) -> Unit) : Action {
    constructor(actionProvider: () -> Action) :
            this({ slack: Slack -> actionProvider().run(slack) })

    override fun run(slack: Slack) {
        thread {
            this.procedure(slack)
        }
    }

    fun then(procedure: () -> Action): FutureAction = FutureAction { slack: Slack ->
        this.procedure(slack)
        procedure().run(slack)
    }

    fun then(action: Action): FutureAction = this.then { action }
}