package io.github.yusaka39.easySlackbot.scheduler

import io.github.yusaka39.easySlackbot.annotations.RunWithInterval
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

internal class Schedule(
        val startHour: Int,
        val startMin: Int,
        tzName: String,
        interval: Long,
        unit: TimeUnit,
        private val intervalCalculationStrategy: Schedule.() -> Long = Schedule.defaultStrategy
) {
    constructor(annotation: RunWithInterval) : this(
            annotation.startHour,
            annotation.startMin,
            annotation.tzName,
            annotation.interval,
            annotation.unit
    )

    private val zoneId: ZoneId = ZoneId.of(ZoneId.SHORT_IDS[tzName] ?: tzName)
    val intervalMillis: Long = TimeUnit.MILLISECONDS.convert(interval, unit)

    fun getDelayToFirstExecution(): Long = this.intervalCalculationStrategy()

    companion object {
        private val defaultStrategy: Schedule.() -> Long = {
            val targetTzNow = ZonedDateTime.now(this.zoneId)
            val firstExecutionCandidate = ZonedDateTime.of(
                    targetTzNow.year, targetTzNow.monthValue, targetTzNow.dayOfMonth,
                    this.startHour, this.startMin, 0, 0, this.zoneId
            )
            val firstExecutionDatetime = if (targetTzNow > firstExecutionCandidate) {
                firstExecutionCandidate.plusDays(1)
            } else {
                firstExecutionCandidate
            }
            firstExecutionDatetime.toInstant().toEpochMilli() - Instant.now().toEpochMilli()
        }
    }
}