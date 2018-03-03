package io.github.yusaka39.easySlackbot.lib

import org.apache.log4j.Logger

object Log {
    private val logger = Logger.getLogger("io.github.yusaka39.easySlackbot")

    fun f(message: String) {
        this.logger.fatal(message)
    }

    fun f(message: String, t: Throwable) {
        this.logger.fatal(message, t)
    }

    fun e(message: String) {
        this.logger.error(message)
    }

    fun e(message: String, t: Throwable) {
        this.logger.error(message, t)
    }

    fun w(message: String) {
        this.logger.warn(message)
    }

    fun w(message: String, t: Throwable) {
        this.logger.warn(message, t)
    }

    fun i(message: String) {
        this.logger.info(message)
    }

    fun i(message: String, t: Throwable) {
        this.logger.info(message, t)
    }

    fun d(message: String) {
        this.logger.debug(message)
    }

    fun d(message: String, t: Throwable) {
        this.logger.debug(message, t)
    }

    fun t(message: String) {
        this.logger.trace(message)
    }

    fun t(message: String, t: Throwable) {
        this.logger.trace(message, t)
    }
}

