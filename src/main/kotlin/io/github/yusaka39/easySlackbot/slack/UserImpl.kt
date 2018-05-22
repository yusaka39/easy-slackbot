package io.github.yusaka39.easySlackbot.slack

import io.github.yusaka39.easySlackbot.api.entity.User

data class UserImpl(
        override val id: String,
        override val userName: String,
        override val realName: String
) : User