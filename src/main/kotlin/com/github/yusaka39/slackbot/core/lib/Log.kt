package com.github.yusaka39.slackbot.core.lib

import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal fun Any.logger(): Lazy<Logger> = lazy { LoggerFactory.getLogger(this.javaClass) }