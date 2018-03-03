package io.github.yusaka39.easySlackbot.router.actions

import io.github.yusaka39.easySlackbot.slack.Attachment
import io.github.yusaka39.easySlackbot.slack.AttachmentBuilder
import io.github.yusaka39.easySlackbot.slack.Channel
import io.github.yusaka39.easySlackbot.slack.Slack
import io.github.yusaka39.easySlackbot.slack.User

class PutAttachmentAction internal constructor(
    private val targetUserName: String?,
    private val targetChannelId: String?,
    private val attachment: Attachment
) : Action {
    override fun run(slack: Slack) = when {
        this.targetUserName != null -> {
            val channelId = slack.getDmChannelIdOrNullByUserName(this.targetUserName)
                    ?: throw IllegalStateException("user ${this.targetUserName} does not exists")
            slack.putAttachmentTo(channelId, this.attachment)
        }
        this.targetChannelId != null -> {
            slack.putAttachmentTo(this.targetChannelId, this.attachment)
        }
        else -> {
            throw IllegalStateException("no target specified")
        }
    }
}

fun putAttachmentToUserAction(targetUserName: String, initializer: AttachmentBuilder.() -> Unit) =
    PutAttachmentAction(
        targetUserName,
        null,
        AttachmentBuilder(initializer).build()
    )

fun putAttachmentToUserAction(targetUser: User, initializer: AttachmentBuilder.() -> Unit) =
    putAttachmentToUserAction(targetUser.userName, initializer)

fun putAttachmentToChannelAction(targetChannelId: String, initializer: AttachmentBuilder.() -> Unit) =
    PutAttachmentAction(
        null,
        targetChannelId,
        AttachmentBuilder(initializer).build()
    )

fun putAttachmentToChannelAction(targetChannel: Channel, initializer: AttachmentBuilder.() -> Unit) =
    putAttachmentToChannelAction(targetChannel.id, initializer)