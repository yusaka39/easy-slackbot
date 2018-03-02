package io.github.yusaka39.easySlackbot.lib

import io.github.yusaka39.easySlackbot.slack.Channel
import io.github.yusaka39.easySlackbot.slack.Message
import io.github.yusaka39.easySlackbot.slack.User
import org.riversun.slacklet.SlackletRequest
import org.riversun.xternal.simpleslackapi.SlackChannel
import org.riversun.xternal.simpleslackapi.SlackUser
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KType

private inline fun <reified T> T?.validateNullability(isNullable: Boolean): T? {
    if (this != null) {
        return this
    }
    if (isNullable) {
        return null
    }
    throw IllegalArgumentException("$this cannot convert to ${T::class.simpleName}.")
}

internal fun String.convertTo(kType: KType): Any? {
    val isNullable = kType.isMarkedNullable
    return when (kType.classifier) {
        Byte::class -> this.toByteOrNull().validateNullability(isNullable)
        Short::class -> this.toShortOrNull().validateNullability(isNullable)
        Int::class -> this.toIntOrNull().validateNullability(isNullable)
        Long::class -> this.toLongOrNull().validateNullability(isNullable)
        Float::class -> this.toFloatOrNull().validateNullability(isNullable)
        Double::class -> this.toDoubleOrNull().validateNullability(isNullable)
        Boolean::class -> when (this) {
            "true" -> true
            "false" -> false
            else -> null
        }.validateNullability(isNullable)
        String::class -> this
        else -> throw IllegalArgumentException("${kType.classifier} is not allowed as parameter")
    }
}

internal inline fun <reified T: Annotation> KAnnotatedElement.isAnnotatedWith() = this.annotations.any { it is T }

internal fun SlackChannel.toChannel(): Channel = Channel(this.id, this.name)

internal fun SlackUser.toUser(): User = User(this.id, this.userName, this.realName)

internal fun SlackletRequest.getMessage(): Message {
    val channel = this.channel.toChannel()
    val text = this.content
    val user = this.sender.toUser()
    val timestamp = this.rawPostedMessage.timestamp.toLong()
    return Message(user, text, channel, timestamp)
}