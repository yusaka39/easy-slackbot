package com.github.yusaka39.slackbot.stdplugin.handler.annotations

import com.github.yusaka39.slackbot.stdplugin.handler.HandlerType

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class HandlerFunction(
        val regex: String,
        val type: Array<HandlerType> = [HandlerType.ListenTo, HandlerType.RespondTo],
        val regexOption: Array<RegexOption> = []
)