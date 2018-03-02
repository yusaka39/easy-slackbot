package io.github.yusaka39.easySlackbot.annotations

import io.github.yusaka39.easySlackbot.router.HandlerType


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class HandlerFunction(val regex: String, val type: Array<HandlerType>)