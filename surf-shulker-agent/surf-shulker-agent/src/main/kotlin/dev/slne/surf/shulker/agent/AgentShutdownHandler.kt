package dev.slne.surf.shulker.agent

import dev.slne.surf.surfapi.core.api.util.logger
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

private const val THREAD_NAME = "Surf-Shulker-Agent-Shutdown-Hook"
var inShutdownProcess = false
    private set

private val log = logger()

fun registerShutdownHook() {
    Runtime.getRuntime().addShutdownHook(Thread({
        runBlocking {
            exitShulker(false)
        }
    }, THREAD_NAME))
}

suspend fun exitShulker(
    cleanShutdown: Boolean = true,
    shouldUpdate: Boolean = false
) {
    if (inShutdownProcess) return
    inShutdownProcess = true

    log.atInfo()
        .log("Shutting down Surf Shulker Agent (cleanShutdown=$cleanShutdown, shouldUpdate=$shouldUpdate)")

    try {

        val services = Agent.runtime.serviceStorage.findAll()
        services.forEach { service ->
            try {
                service.shutdown(cleanShutdown)
            } catch (e: Exception) {
                log.atSevere().withCause(e)
                    .log("An error occurred while shutting down service ${service.name}")
            }
        }

        Agent.moduleProvider.unloadModules()
        Agent.close()
    } catch (e: Exception) {
        log.atSevere().withCause(e)
            .log("An error occurred during Surf Shulker Agent shutdown")
    }

    log.atInfo()
        .log("Surf Shulker Agent has been shut down")

    if (shouldUpdate) {
        Updater.update()
    }

    if (Thread.currentThread().name != THREAD_NAME) {
        exitProcess(0)
    }
}
