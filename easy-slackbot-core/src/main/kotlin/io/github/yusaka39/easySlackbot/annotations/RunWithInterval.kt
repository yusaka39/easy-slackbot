package io.github.yusaka39.easySlackbot.annotations

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