package io.github.yusaka39.easySlackbot.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ListenTo(val regexp: String)