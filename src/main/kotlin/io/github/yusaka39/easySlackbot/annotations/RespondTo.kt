package io.github.yusaka39.easySlackbot.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class RespondTo(val regexp: String)