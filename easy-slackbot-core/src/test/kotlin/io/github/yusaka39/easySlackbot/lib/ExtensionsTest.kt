package io.github.yusaka39.easySlackbot.lib

import com.ullink.slack.simpleslackapi.SlackChannel
import com.ullink.slack.simpleslackapi.SlackPersona
import com.ullink.slack.simpleslackapi.SlackUser
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted
import io.github.yusaka39.easySlackbot.slack.AttachmentImpl
import io.github.yusaka39.easySlackbot.slack.ChannelImpl
import io.github.yusaka39.easySlackbot.slack.MessageImpl
import io.github.yusaka39.easySlackbot.slack.UserImpl
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class ExtensionsTest {

    private val KClass<*>.type: KType
        get() = this.starProjectedType

    private val KClass<*>.nullableType: KType
        get() = this.type.withNullability(true)

    @Test
    fun convertToReturnsBytesCorrectly() {
        assertEquals(10.toByte(), "10".convertTo(Byte::class.type))
    }

    @Test
    fun convertToByteFailsCorrectly() {
        assertFailsWith<IllegalArgumentException> {
            "256".convertTo(Byte::class.type)
        }
        assertFailsWith<IllegalArgumentException> {
            "foo".convertTo(Byte::class.type)
        }
        assertEquals(null, "256".convertTo(Byte::class.nullableType))
        assertEquals(null, "foo".convertTo(Byte::class.nullableType))
    }

    @Test
    fun convertToReturnsShortCorrectly() {
        assertEquals(512.toShort(), "512".convertTo(Short::class.type))
    }

    @Test
    fun convertToShortFailsCorrectly() {
        assertFailsWith<IllegalArgumentException> {
            "32768".convertTo(Short::class.type)
        }
        assertFailsWith<IllegalArgumentException> {
            "foo".convertTo(Short::class.type)
        }
        assertEquals(null, "32768".convertTo(Short::class.nullableType))
        assertEquals(null, "foo".convertTo(Short::class.nullableType))
    }

    @Test
    fun convertToReturnsIntCorrectly() {
        assertEquals(1024, "1024".convertTo(Int::class.type))
    }

    @Test
    fun convertToIntFailsCorrectly() {
        assertFailsWith<IllegalArgumentException> {
            "2147483648".convertTo(Int::class.type)
        }
        assertFailsWith<IllegalArgumentException> {
            "foo".convertTo(Int::class.type)
        }
        assertEquals(null, "2147483648".convertTo(Int::class.nullableType))
        assertEquals(null, "foo".convertTo(Int::class.nullableType))
    }

    @Test
    fun convertToReturnsLongCorrectly() {
        assertEquals(2048L, "2048".convertTo(Long::class.type))
    }

    @Test
    fun convertToLongFailsCorrectly() {
        assertFailsWith<IllegalArgumentException> {
            "9223372036854775808".convertTo(Long::class.type)
        }
        assertFailsWith<IllegalArgumentException> {
            "foo".convertTo(Long::class.type)
        }
        assertEquals(null, "9223372036854775808".convertTo(Long::class.nullableType))
        assertEquals(null, "foo".convertTo(Long::class.nullableType))
    }

    @Test
    fun convertToReturnsFloatCorrectly() {
        assertEquals(0F, "0.0".convertTo(Float::class.type))
    }

    @Test
    fun convertToFloatFailsCorrectly() {
        assertFailsWith<IllegalArgumentException> {
            "foo".convertTo(Float::class.type)
        }
        assertEquals(null, "foo".convertTo(Float::class.nullableType))
    }

    @Test
    fun convertToReturnsDoubleCorrectly() {
        assertEquals(0.0, "0.0".convertTo(Double::class.type))
    }

    @Test
    fun convertToDoubleFailsCorrectly() {
        assertFailsWith<IllegalArgumentException> {
            "foo".convertTo(Double::class.type)
        }
        assertEquals(null, "foo".convertTo(Double::class.nullableType))
    }

    @Test
    fun convertToReturnsBooleanCorrectly() {
        assertEquals(true, "true".convertTo(Boolean::class.type))
        assertEquals(false, "false".convertTo(Boolean::class.type))
    }

    @Test
    fun convertToBooleanFailsCorrectly() {
        assertFailsWith<IllegalArgumentException> {
            "foo".convertTo(Boolean::class.type)
        }
        assertEquals(null, "foo".convertTo(Boolean::class.nullableType))
    }

    @Test
    fun convertToStringReturnsValueEqualsToReceiver() {
        assertEquals("foo", "foo".convertTo(String::class.type))
    }

    @Test
    fun convertToFailsWhenUnsupportedTypeGiven() {
        class Foobar
        assertFailsWith<IllegalArgumentException> { "foobar".convertTo(Foobar::class.type) }
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.CLASS)
    annotation class AnnotationForTesting1

    @Test
    fun isAnnotatedWithReturnsTrueWhenReceiverIsAnnotated() {
        @AnnotationForTesting1
        class Foo
        assertTrue(Foo::class.isAnnotatedWith<AnnotationForTesting1>())
    }

    @Retention(AnnotationRetention.SOURCE)
    @Target(AnnotationTarget.CLASS)
    annotation class AnnotationForTesting2

    @Test
    fun isAnnotatedWithReturnFalseWhenReceiverIsNotAnnotated() {
        class Foo
        assertFalse { Foo::class.isAnnotatedWith<AnnotationForTesting2>() }

        @AnnotationForTesting2
        class Bar
        assertFalse { Bar::class.isAnnotatedWith<AnnotationForTesting2>() }
    }

    private val testChannel = SlackChannel("Cgeneral", "general", "", "", false, true, false)

    @Test
    fun toChannelConvertSlackChannelToChannel() {
        val chan = this.testChannel
        assertEquals(ChannelImpl("Cgeneral", "general"), chan.toChannel())
    }

    private class TestUser : SlackUser {
        override fun getUserSkype(): String {
            TODO("not implemented")
        }

        override fun isOwner(): Boolean {
            TODO("not implemented")
        }

        override fun isDeleted(): Boolean {
            TODO("not implemented")
        }

        override fun isBot(): Boolean {
            TODO("not implemented")
        }

        override fun getTimeZone(): String {
            TODO("not implemented")
        }

        override fun getUserName(): String = "awesomekotlin"

        override fun getId(): String = "Uabcdefg"

        override fun getTimeZoneLabel(): String {
            TODO("not implemented")
        }

        override fun getUserTitle(): String {
            TODO("not implemented")
        }

        override fun isUltraRestricted(): Boolean {
            TODO("not implemented")
        }

        override fun getUserMail(): String {
            TODO("not implemented")
        }

        override fun getUserPhone(): String {
            TODO("not implemented")
        }

        override fun getTimeZoneOffset(): Int {
            TODO("not implemented")
        }

        override fun isPrimaryOwner(): Boolean {
            TODO("not implemented")
        }

        override fun isRestricted(): Boolean {
            TODO("not implemented")
        }

        override fun getPresence(): SlackPersona.SlackPresence {
            TODO("not implemented")
        }

        override fun isAdmin(): Boolean {
            TODO("not implemented")
        }

        override fun getRealName(): String = "John Doe"

    }

    @Test
    fun toUserConvertSlackUserToUser() {
        val user = TestUser()
        assertEquals(UserImpl("Uabcdefg", "awesomekotlin", "John Doe"), user.toUser())
    }

    @Test
    fun toSlackAttachmentConvertAttachmentToSlackAttachment() {
        val slackAttachment = AttachmentImpl(
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
                AttachmentImpl.Field("title", "value", true)
            ),
            listOf(
                AttachmentImpl.Action("type", "text", "url", "style")
            ),
            mapOf("misc" to "foobar")
        ).toSlackAttachment()

        assertEquals("fallback", slackAttachment.fallback)
        assertEquals("color", slackAttachment.color)
        assertEquals("authorName", slackAttachment.authorName)
        assertEquals("authorLink", slackAttachment.authorLink)
        assertEquals("authorIcon", slackAttachment.authorIcon)
        assertEquals("title", slackAttachment.title)
        assertEquals("titleLink", slackAttachment.titleLink)
        assertEquals("text", slackAttachment.text)
        assertEquals("preText", slackAttachment.pretext)
        assertEquals("imageUrl", slackAttachment.imageUrl)
        assertEquals("thumbnailUrl", slackAttachment.thumbUrl)
        assertEquals("footer", slackAttachment.footer)
        assertEquals("footerIcon", slackAttachment.footerIcon)

        assertEquals(1, slackAttachment.fields.size)
        slackAttachment.fields[0].let {
            assertEquals("title", it.title)
            assertEquals("value", it.value)
            assertEquals(true, it.isShort)
        }

        assertEquals(1, slackAttachment.actions.size)
        slackAttachment.actions[0].let {
            assertEquals("type", it.type)
            assertEquals("text", it.text)
            assertEquals("url", it.value)
            assertEquals("style", it.style)
        }

        assertEquals(mapOf("misc" to "foobar"), slackAttachment.miscRootFields)
    }

    @Test
    fun toMessageReturnsCorrectMessage() {
        val postedMessage = SlackMessagePosted(
            "kotlin is awesome.",
            null,
            TestUser(),
            this.testChannel,
            "12345678.123456",
            SlackMessagePosted.MessageSubType.UNKNOWN
        )

        assertEquals(
            MessageImpl(TestUser().toUser(), "kotlin is awesome.", this.testChannel.toChannel(), "12345678.123456"),
            postedMessage.toMessage()
        )
    }
}



