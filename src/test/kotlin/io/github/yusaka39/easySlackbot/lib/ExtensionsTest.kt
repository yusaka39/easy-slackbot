package io.github.yusaka39.easySlackbot.lib

import org.junit.Test
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


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
}