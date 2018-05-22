package com.github.yusaka39.slackbot.stdplugin.actions

import com.github.yusaka39.slackbot.api.entity.Action
import com.github.yusaka39.slackbot.api.entity.Attachment
import com.github.yusaka39.slackbot.api.entity.Channel
import com.github.yusaka39.slackbot.api.entity.Slack
import com.github.yusaka39.slackbot.api.entity.User
import com.github.yusaka39.slackbot.core.slack.AttachmentListBuilder

class PutAttachmentAction internal constructor(
        private val targetUserName: String?,
        private val targetChannelId: String?,
        private val attachments: List<Attachment>
) : Action {
    override fun run(slack: Slack) = when {
        this.targetUserName != null -> {
            val channelId = slack.getDmChannelIdOrNullByUserName(this.targetUserName)
                    ?: throw IllegalStateException("user ${this.targetUserName} does not exists")
            slack.putAttachmentTo(channelId, *this.attachments.toTypedArray())
        }
        this.targetChannelId != null -> {
            slack.putAttachmentTo(this.targetChannelId, *this.attachments.toTypedArray())
        }
        else -> {
            throw IllegalStateException("no target specified")
        }
    }
}

fun putAttachmentToUserAction(targetUserName: String, initializer: AttachmentListBuilder.() -> Unit) =
        PutAttachmentAction(
                targetUserName,
                null,
                AttachmentListBuilder(initializer).build()
        )

fun putAttachmentToUserAction(targetUser: User, initializer: AttachmentListBuilder.() -> Unit) =
        putAttachmentToUserAction(targetUser.userName, initializer)

fun putAttachmentToChannelAction(targetChannelId: String, initializer: AttachmentListBuilder.() -> Unit) =
        PutAttachmentAction(
                null,
                targetChannelId,
                AttachmentListBuilder(initializer).build()
        )

fun putAttachmentToChannelAction(targetChannel: Channel, initializer: AttachmentListBuilder.() -> Unit) =
        putAttachmentToChannelAction(targetChannel.id, initializer)