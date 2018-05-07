package io.github.yusaka39.easySlackbot.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class GroupParam(val group: Int)