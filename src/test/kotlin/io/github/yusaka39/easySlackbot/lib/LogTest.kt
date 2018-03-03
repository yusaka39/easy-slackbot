package io.github.yusaka39.easySlackbot.lib

import org.junit.Test
import org.slf4j.LoggerFactory
import kotlin.test.assertEquals

class LogTest {
    @Test
    fun loggerReturnsCorrectProperty() {
        val logger by this.logger()

        assertEquals(logger, LoggerFactory.getLogger(LogTest::class.java))
    }
}