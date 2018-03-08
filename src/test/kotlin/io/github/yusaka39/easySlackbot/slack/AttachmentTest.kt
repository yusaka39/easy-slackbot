package io.github.yusaka39.easySlackbot.slack

import kotlin.test.Test
import kotlin.test.assertEquals

class AttachmentTest {
    private val testAttachment = Attachment(
        "fallback",
        "color",
        "authorName",
        "authorLink",
        "authorIcon",
        "title",
        "titleLink",
        "text",
        "preText",
        "imageUrl",
        "thumbnailUrl",
        "footer",
        "footerIcon",
        listOf(
            Attachment.Field("title", "value", true)
        ),
        listOf(
            Attachment.Action("type", "text", "url")
        ),
        mapOf("misc" to "foobar")
    )

    @Test
    fun attachmentPropertiesWorksCorrectly() {
        assertEquals("fallback", this.testAttachment.fallback)
        assertEquals("color", this.testAttachment.color)
        assertEquals("authorName", this.testAttachment.authorName)
        assertEquals("authorLink", this.testAttachment.authorLink)
        assertEquals("authorIcon", this.testAttachment.authorIcon)
        assertEquals("title", this.testAttachment.title)
        assertEquals("titleLink", this.testAttachment.titleLink)
        assertEquals("text", this.testAttachment.text)
        assertEquals("preText", this.testAttachment.preText)
        assertEquals("imageUrl", this.testAttachment.imageUrl)
        assertEquals("thumbnailUrl", this.testAttachment.thumbnailUrl)
        assertEquals("footer", this.testAttachment.footer)
        assertEquals("footerIcon", this.testAttachment.footerIcon)

        assertEquals(1, this.testAttachment.fields.size)
        this.testAttachment.fields[0].let {
            assertEquals("title", it.title)
            assertEquals("value", it.value)
            assertEquals(true, it.isShort)
        }

        assertEquals(1, this.testAttachment.actions.size)
        this.testAttachment.actions[0].let {
            assertEquals("type", it.type)
            assertEquals("text", it.text)
            assertEquals("url", it.url)
        }

        assertEquals(mapOf("misc" to "foobar"), this.testAttachment.misc)
    }

    @Test
    fun buildersWorksFine() {
        val attachment = AttachmentBuilder {
            fallback = "fallback"
            color = "color"
            authorName = "authorName"
            authorLink = "authorLink"
            authorIcon = "authorIcon"
            title = "title"
            titleLink = "titleLink"
            text = "text"
            preText = "preText"
            imageUrl = "imageUrl"
            thumbnailUrl = "thumbnailUrl"
            footer = "footer"
            footerIcon = "footerIcon"

            field {
                title = "title"
                value = "value"
                isShort = true
            }

            action {
                type = "type"
                text = "text"
                url = "url"
            }

            misc("misc", "foobar")
        }.build()
        assertEquals(this.testAttachment, attachment)
    }
}