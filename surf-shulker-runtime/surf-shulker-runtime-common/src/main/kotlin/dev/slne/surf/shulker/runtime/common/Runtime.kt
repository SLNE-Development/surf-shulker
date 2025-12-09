package dev.slne.surf.shulker.runtime.common

import dev.slne.surf.shulker.agent.Agent
import dev.slne.surf.shulker.api.service.Service
import dev.slne.surf.shulker.runtime.common.abstract.AbstractServiceStatsJob
import dev.slne.surf.surfapi.core.api.util.logger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.time.OffsetDateTime

abstract class Runtime {
    val startedAt = OffsetDateTime.now()

    private val log = logger()
    val runtimeScope =
        CoroutineScope(SupervisorJob() + CoroutineName("shulker-runtime") + CoroutineExceptionHandler { context, throwable ->
            log.atSevere().withCause(throwable)
                .log("Exception in context ${context[CoroutineName]}")
        })

    abstract val serviceStorage: RuntimeServiceStorage<*>
    abstract val groupStorage: RuntimeGroupStorage
    abstract val factory: RuntimeFactory<out Service>
    abstract val expender: RuntimeExpender<Service>
    abstract val templateStorage: RuntimeTemplateStorage<*, Service>
    abstract val configHolder: RuntimeConfigHolder
    abstract val detectedLocalAddress: String
    abstract val serviceStatsThread: AbstractServiceStatsJob<*>

    open fun init() {
        Agent.bootstrap()

        serviceStatsThread.start()
    }

    open fun boot() {

    }

    open fun shutdown() {

    }

    abstract fun sendCommand(command: String)

    companion object {
        fun create(): Runtime = TODO("IMPLEMENT")
    }
}