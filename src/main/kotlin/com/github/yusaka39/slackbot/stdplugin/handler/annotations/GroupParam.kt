package com.github.yusaka39.slackbot.stdplugin.handler.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class GroupParam(val group: Int)