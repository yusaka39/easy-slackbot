package io.github.yusaka39.easySlackbot.lib

import kotlin.test.Test
import kotlin.test.assertEquals

class LazyMapTest {
    @Test
    fun getReturnsCorrectValue() {
        val lm = LazyMap<Int, Int?> { if (it != 0) it * it else null }
        assertEquals(4, lm[2])
        assertEquals(4, lm.getOrDefault(2, 9))
        assertEquals(4, lm.getOrElse(2) { 9 })
        assertEquals(9, lm.getOrDefault(0, 9))
        assertEquals(9, lm.getOrElse(0) { 9 })
    }
}