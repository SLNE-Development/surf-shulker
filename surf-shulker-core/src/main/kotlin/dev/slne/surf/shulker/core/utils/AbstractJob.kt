package dev.slne.surf.shulker.core.utils

import dev.slne.surf.surfapi.core.api.util.logger
import kotlinx.coroutines.*

abstract class AbstractJob(
    val name: String
) {
    private val log = logger()
    private val scope =
        CoroutineScope(SupervisorJob() + CoroutineName(name) + CoroutineExceptionHandler { context, throwable ->
            log.atSevere().withCause(throwable)
                .log("An error occurred in $name job: ${context[CoroutineName]}")
        })

    private var job: Job? = null

    abstract suspend fun run()

    fun start() {
        if (job != null) {
            log.atWarning().log("Job $name is already running.")
            return
        }

        job = scope.launch {
            run()
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}