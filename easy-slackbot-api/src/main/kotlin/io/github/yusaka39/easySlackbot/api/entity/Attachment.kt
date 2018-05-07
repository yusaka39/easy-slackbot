package io.github.yusaka39.easySlackbot.api.entity

interface Attachment {
    interface Field {
        val title: String
        val value: String
        val isShort: Boolean
    }

    interface Action {
        val type: String
        val text: String
        val url: String
        val style: String?
    }

    val fallback: String?
    val color: String?
    val authorName: String?
    val authorLink: String?
    val authorIcon: String?
    val title: String?
    val titleLink: String?
    val text: String?
    val preText: String?
    val imageUrl: String?
    val thumbnailUrl: String?
    val footer: String?
    val footerIcon: String?
    val fields: List<Field>
    val actions: List<Action>
    val misc: Map<String, String>
}