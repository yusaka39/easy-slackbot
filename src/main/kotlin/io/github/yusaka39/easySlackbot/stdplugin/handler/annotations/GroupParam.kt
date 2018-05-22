package io.github.yusaka39.easySlackbot.stdplugin.handler.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class GroupParam(val group: Int)