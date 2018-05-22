package io.github.yusaka39.easySlackbot.stdplugin.handler.annotations

import io.github.yusaka39.easySlackbot.stdplugin.handler.HandlerType

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class HandlerFunction(
        val regex: String,
        val type: Array<HandlerType> = [HandlerType.ListenTo, HandlerType.RespondTo],
        val regexOption: Array<RegexOption> = []
)