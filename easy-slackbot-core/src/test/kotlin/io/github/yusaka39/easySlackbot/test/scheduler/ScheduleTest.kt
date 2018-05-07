package io.github.yusaka39.easySlackbot.test.scheduler

import io.github.yusaka39.easySlackbot.annotations.RunWithInterval
import io.github.yusaka39.easySlackbot.scheduler.Schedule
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import kotlin.reflect.full.findAnnotation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScheduleTest {
    @Test
    fun scheduleReturnsCorrectDelay() {
        Schedule(0, 0, "UTC", 100, TimeUnit.MILLISECONDS).testGetDelayToFIrstExecution()
        Schedule(23, 59, "UTC", 100, TimeUnit.MILLISECONDS).testGetDelayToFIrstExecution()
    }

    private fun Schedule.testGetDelayToFIrstExecution() {
        val interval = this.getDelayToFirstExecution()
        val now = Instant.now()
        val nowDatetime = ZonedDateTime.ofInstant(now, ZoneId.of("UTC"))
        val executionDatetime = ZonedDateTime.ofInstant(now.plusMillis(interval), ZoneId.of("UTC"))
        assertTrue(nowDatetime < executionDatetime)
        assertTrue(executionDatetime.dayOfMonth - nowDatetime.dayOfMonth <= 1)
        assertTrue(executionDatetime.hour == this.startHour)
        assertTrue(executionDatetime.minute == this.startMin)
        assertTrue(executionDatetime.second == 0)
    }

    private class TestClass {
        @get:RunWithInterval(12, 30, "UTC", 100, TimeUnit.MILLISECONDS)
        val testProperty = null
    }

    @Test
    fun scheduleConstructorWorksFine() {
        val annotation = TestClass::testProperty.getter.findAnnotation<RunWithInterval>()
                ?: throw AssertionError()
        val schedule = Schedule(annotation)
        assertEquals(12, schedule.startHour)
        assertEquals(30, schedule.startMin)
        assertEquals(100, schedule.intervalMillis)
        schedule.testGetDelayToFIrstExecution()
    }

    @Test
    fun schedulePropertyWorksFine() {
        val schedule = Schedule(20, 10, "JST", 1000, TimeUnit.HOURS)
        assertEquals(20, schedule.startHour)
        assertEquals(10, schedule.startMin)
    }

    @Test
    fun intervalMillisReturnsCorrectInterval() {
        assertEquals(0, Schedule(0, 0, "UTC", 1, TimeUnit.NANOSECONDS).intervalMillis)
        assertEquals(1, Schedule(0, 0, "UTC", 1, TimeUnit.MILLISECONDS).intervalMillis)
        assertEquals(1000, Schedule(0, 0, "UTC", 1, TimeUnit.SECONDS).intervalMillis)
        assertEquals(60000, Schedule(0, 0, "UTC", 1, TimeUnit.MINUTES).intervalMillis)
        assertEquals(3600000, Schedule(0, 0, "UTC", 1, TimeUnit.HOURS).intervalMillis)
        assertEquals(3600000 * 24, Schedule(0, 0, "UTC", 1, TimeUnit.DAYS).intervalMillis)
    }
}