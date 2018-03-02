package io.github.yusaka39.easySlackbot.slack

data class User(
        val id: String,
        val userName: String,
        val realName: String
) {
    val replyString: String
        get() = "<@${this.id}>"
}