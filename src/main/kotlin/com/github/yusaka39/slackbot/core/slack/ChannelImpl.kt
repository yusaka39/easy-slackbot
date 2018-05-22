package com.github.yusaka39.slackbot.core.slack

import com.github.yusaka39.slackbot.api.entity.Channel

data class ChannelImpl(override val id: String, override val name: String?) : Channel