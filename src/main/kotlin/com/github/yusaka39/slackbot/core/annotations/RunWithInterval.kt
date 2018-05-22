package com.github.yusaka39.slackbot.core.annotations

import java.util.concurrent.TimeUnit

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class RunWithInterval(
        val startHour: Int,
        val startMin: Int,
        val tzName: String,
        val interval: Long,
        val unit: TimeUnit
)