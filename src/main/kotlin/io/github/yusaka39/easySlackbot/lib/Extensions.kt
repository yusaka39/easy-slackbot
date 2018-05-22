package io.github.yusaka39.easySlackbot.lib

import com.ullink.slack.simpleslackapi.SlackAction
import com.ullink.slack.simpleslackapi.SlackAttachment
import com.ullink.slack.simpleslackapi.SlackChannel
import com.ullink.slack.simpleslackapi.SlackUser
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted
import io.github.yusaka39.easySlackbot.api.entity.Attachment
import io.github.yusaka39.easySlackbot.api.entity.Channel
import io.github.yusaka39.easySlackbot.api.entity.Message
import io.github.yusaka39.easySlackbot.api.entity.User
import io.github.yusaka39.easySlackbot.slack.AttachmentBuilder
import io.github.yusaka39.easySlackbot.slack.ChannelImpl
import io.github.yusaka39.easySlackbot.slack.MessageImpl
import io.github.yusaka39.easySlackbot.slack.UserImpl
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KType




internal inline fun <reified T : Annotation> KAnnotatedElement.isAnnotatedWith() = this.annotations.any { it is T }

internal fun SlackChannel.toChannel(): Channel = ChannelImpl(this.id, this.name)

internal fun SlackUser.toUser(): User = UserImpl(this.id, this.userName, this.realName)

internal fun SlackMessagePosted.toMessage(): Message = MessageImpl(
        this.user.toUser(),
        this.messageContent,
        this.channel.toChannel(),
        this.timestamp,
        this.attachments?.map(SlackAttachment::toAttachment) ?: emptyList()
)

internal fun SlackAttachment.toAttachment(): Attachment {
    val attachment = this
    return AttachmentBuilder {
        this.titleLink = attachment.titleLink
        this.thumbnailUrl = attachment.thumbUrl
        this.authorName = attachment.authorName
        this.authorLink = attachment.authorLink
        this.authorIcon = attachment.authorIcon
        this.footer = attachment.footer
        this.footerIcon = attachment.footerIcon
        this.imageUrl = attachment.imageUrl
        this.color = attachment.color
        attachment.actions.forEach {
            action {
                this.url = it.value
                this.type = it.type
                this.style = it.style
                this.text = it.text
            }
        }
        attachment.fields.forEach {
            field {
                this.title = it.title
                this.value = it.value
                this.isShort = it.isShort
            }
        }
        attachment.miscRootFields.forEach { k, v ->
            misc(k, v)
        }
    }.build()
}

internal fun Attachment.toSlackAttachment(): SlackAttachment {
    return SlackAttachment(this.title, this.fallback, this.text, this.preText).apply {
        val attachment = this@toSlackAttachment
        attachment.actions.forEach {
            val action = SlackAction(null, it.text, it.type, it.url).apply {
                this.style = it.style
            }
            this.addAction(action)
        }
        attachment.fields.forEach {
            this.addField(it.title, it.value, it.isShort)
        }
        attachment.misc.forEach { key, value ->
            this.addMiscField(key, value)
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

