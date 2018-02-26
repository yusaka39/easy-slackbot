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
        assertEquals(10.toByte(), "10".convertTo(Byte::class.starProjectedType))
    }

    @Test
    fun convertToFailsWhenFailedToConvertStringToByte() {
        assertFailsWith<IllegalArgumentException> {
            "256".convertTo(Byte::class.starProjectedType)
        }
        assertFailsWith<IllegalArgumentException> {
            "foo".convertTo(Byte::class.starProjectedType)
        }
    }

    @Test
    fun convertToReturnsNullIfAllowed() {
        assertEquals(null, "256".convertTo(Byte::class.nullableType))
        assertEquals(null, "foo".convertTo(Byte::class.nullableType))
    }
}