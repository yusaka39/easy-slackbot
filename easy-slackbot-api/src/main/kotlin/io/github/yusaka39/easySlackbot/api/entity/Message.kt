package io.github.yusaka39.easySlackbot.api.entity

interface Message {
    val user: User
    val text: String
    val channel: Channel
    val timestamp: String
    val attachments: List<Attachment>
}