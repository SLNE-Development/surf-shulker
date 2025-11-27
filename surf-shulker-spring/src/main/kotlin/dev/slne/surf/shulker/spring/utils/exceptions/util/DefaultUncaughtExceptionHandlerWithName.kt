package dev.slne.surf.shulker.spring.utils.exceptions.util

import com.google.common.flogger.FluentLogger

class DefaultUncaughtExceptionHandlerWithName(
    private val log: FluentLogger
) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(thread: Thread?, exception: Throwable?) {
        log.atSevere()
            .withCause(exception)
            .log("Uncaught exception in thread '%s'", thread?.name ?: "unknown")
    }
}