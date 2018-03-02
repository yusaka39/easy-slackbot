package io.github.yusaka39.easySlackbot.slack

data class Message(
        val user: User,
        val text: String,
        val channel: Channel,
        val timestamp: String
)