package com.github.yusaka39.slackbot.core.slack

import com.github.yusaka39.slackbot.api.entity.User

data class UserImpl(
        override val id: String,
        override val userName: String,
        override val realName: String
) : User