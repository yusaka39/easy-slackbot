package io.github.yusaka39.easySlackbot.lib

import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal fun Any.logger(): Lazy<Logger> = lazy { LoggerFactory.getLogger(this.javaClass) }