package io.github.yusaka39.easySlackbot.lib

import io.github.yusaka39.easySlackbot.slack.Attachment
import io.github.yusaka39.easySlackbot.slack.Channel
import io.github.yusaka39.easySlackbot.slack.Message
import io.github.yusaka39.easySlackbot.slack.User
import org.riversun.slacklet.SlackletRequest
import org.riversun.xternal.simpleslackapi.SlackAttachment
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
    val timestamp = this.rawPostedMessage.timestamp
    return Message(user, text, channel, timestamp)
}

internal fun Attachment.toSlackAttachment(): SlackAttachment {
    return SlackAttachment(this.title, this.fallback, this.text, this.preText).apply {
        val attachment = this@toSlackAttachment
        attachment.actions.forEach {
            this.addAction(null, it.url, it.text, it.type)
        }
        attachment.fields.forEach {
            this.addField(it.title, it.value, it.isShort)
        }

        this.titleLink = attachment.titleLink
        this.thumbUrl = attachment.thumbnailUrl
        this.authorName = attachment.authorName
        this.authorLink = attachment.authorLink
        this.authorIcon = attachment.authorIcon
        this.footer = attachment.footer
        this.footerIcon = attachment.footerIcon
        this.imageUrl = attachment.imageUrl
        this.color = attachment.color
    }
}