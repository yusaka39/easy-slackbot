package io.github.yusaka39.easySlackbot.slack

data class Attachment(
        val fallback: String?,
        val color: String?,
        val authorName: String?,
        val authorLink: String?,
        val authorIcon: String?,
        val title: String?,
        val titleLink: String?,
        val text: String?,
        val preText: String?,
        val imageUrl: String?,
        val thumbnailUrl: String?,
        val footer: String?,
        val footerIcon: String?,
        val fields: List<Field>,
        val actions: List<Action>
) {
    data class Field(
            val title: String,
            val value: String,
            val isShort: Boolean
    )

    data class Action(
            val type: String,
            val text: String,
            val url: String
    )
}

@DslMarker
@Target(AnnotationTarget.CLASS)
annotation class Builder

@Builder
class AttachmentBuilder(initializer: AttachmentBuilder.() -> Unit) {
    var fallback: String? = null
    var color: String? = null
    var authorName: String? = null
    var authorLink: String? = null
    var authorIcon: String? = null
    var title: String? = null
    var titleLink: String? = null
    var text: String? = null
    var preText: String? = null
    var imageUrl: String? = null
    var thumbnailUrl: String? = null
    var footer: String? = null
    var footerIcon: String? = null
    var fields: List<Attachment.Field> = emptyList()
    var actions: List<Attachment.Action> = emptyList()

    init {
        this.initializer()
    }

    fun field(initializer: FieldBuilder.() -> Unit) {
        fields += FieldBuilder(initializer).build()
    }

    fun action(initializer: ActionBuilder.() -> Unit) {
        actions += ActionBuilder(initializer).build()
    }

    fun build(): Attachment = Attachment(
            this.fallback, this.color, this.authorName, this.authorLink, this.authorIcon, this.title, this.titleLink,
            this.text, this.preText, this.imageUrl, this.thumbnailUrl, this.footer, this.footerIcon, this.fields,
            this.actions
    )
}

@Builder
class FieldBuilder(initializer: FieldBuilder.() -> Unit) {
    var title: String = ""
    var value: String = ""
    var isShort: Boolean = false

    init {
        this.initializer()
    }

    fun build(): Attachment.Field = Attachment.Field(this.title, this.value, this.isShort)
}

@Builder
class ActionBuilder(initializer: ActionBuilder.() -> Unit) {
    var type: String = ""
    var text: String = ""
    var url: String = ""

    init {
        this.initializer()
    }

    fun build(): Attachment.Action = Attachment.Action(this.type, this.text, this.url)
}
