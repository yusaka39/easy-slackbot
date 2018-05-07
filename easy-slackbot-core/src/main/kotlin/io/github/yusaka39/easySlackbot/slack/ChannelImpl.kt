package io.github.yusaka39.easySlackbot.slack

import io.github.yusaka39.easySlackbot.api.entity.Channel

data class ChannelImpl(override val id: String, override val name: String?) : Channel