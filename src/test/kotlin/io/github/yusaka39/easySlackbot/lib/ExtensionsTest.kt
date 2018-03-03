package io.github.yusaka39.easySlackbot.lib

import io.github.yusaka39.easySlackbot.slack.Attachment
import io.github.yusaka39.easySlackbot.slack.Channel
import io.github.yusaka39.easySlackbot.slack.Message
import io.github.yusaka39.easySlackbot.slack.User
import org.junit.Test
import org.riversun.slacklet.SlackletRequest
import org.riversun.xternal.simpleslackapi.SlackAttachment
import org.riversun.xternal.simpleslackapi.SlackBot
import org.riversun.xternal.simpleslackapi.SlackChannel
import org.riversun.xternal.simpleslackapi.SlackFile
import org.riversun.xternal.simpleslackapi.SlackPersona
import org.riversun.xternal.simpleslackapi.SlackUser
import org.riversun.xternal.simpleslackapi.events.SlackEventType
import org.riversun.xternal.simpleslackapi.events.SlackMessagePosted
import java.util.ArrayList
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.isAccessible
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

    private class TestChannel : SlackChannel {
        override fun getPurpose(): String {
            TODO("not implemented")
        }

        override fun isArchived(): Boolean {
            TODO("not implemented")
        }

        override fun getName(): String = "general"

        override fun getId(): String = "Cgeneral"

        override fun getType(): SlackChannel.SlackChannelType {
            TODO("not implemented")
        }

        override fun isDirect(): Boolean {
            TODO("not implemented")
        }

        override fun getMembers(): MutableCollection<SlackUser> {
            TODO("not implemented")
        }

        override fun isMember(): Boolean {
            TODO("not implemented")
        }

        override fun getTopic(): String {
            TODO("not implemented")
        }
    }

    @Test
    fun toChannelConvertSlackChannelToChannel() {
        val chan = TestChannel()
        assertEquals(Channel("Cgeneral", "general"), chan.toChannel())
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
        assertEquals(User("Uabcdefg", "awesomekotlin", "John Doe"), user.toUser())
    }

    @Test
    fun toSlackAttachmentConvertAttachmentToSlackAttachment() {
        val slackAttachment = Attachment(
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
            )
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
        }
    }

    @Test
    fun getMessageReturnsCorrectMessage() {
        val postedMessage = object : SlackMessagePosted {
            override fun getTimeStamp(): String = "12345678.123456"

            override fun getSender(): SlackUser = TestUser()

            override fun getMessageContent(): String = "kotlin is awesome."

            override fun getChannel(): SlackChannel = TestChannel()

            override fun getMessageSubType(): SlackMessagePosted.MessageSubType {
                TODO("not implemented")
            }

            override fun getJsonSource(): String {
                TODO("not implemented")
            }

            override fun getSlackFile(): SlackFile {
                TODO("not implemented")
            }

            override fun getReactions(): MutableMap<String, Int> {
                TODO("not implemented")
            }

            override fun getBot(): SlackBot {
                TODO("not implemented")
            }

            override fun getEventType(): SlackEventType {
                TODO("not implemented")
            }

            override fun getTimestamp(): String = this.timeStamp

            override fun getTotalCountOfReactions(): Int {
                TODO("not implemented")
            }

            override fun getAttachments(): ArrayList<SlackAttachment> {
                TODO("not implemented")
            }

        }
        val request = SlackletRequest::class.constructors.first().let {
            it.isAccessible = true
            it.call(null, postedMessage, null)
        }
        assertEquals(
            Message(TestUser().toUser(), "kotlin is awesome.", TestChannel().toChannel(), "12345678.123456"),
            request.getMessage()
        )
    }
}


