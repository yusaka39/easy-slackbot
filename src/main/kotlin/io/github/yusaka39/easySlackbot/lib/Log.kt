package io.github.yusaka39.easySlackbot.lib

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Any.logger(): Lazy<Logger> = lazy { LoggerFactory.getLogger(this.javaClass) }