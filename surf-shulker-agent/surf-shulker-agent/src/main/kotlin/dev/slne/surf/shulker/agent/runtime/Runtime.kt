package dev.slne.surf.shulker.agent.runtime

import dev.slne.surf.shulker.agent.Agent
import java.time.OffsetDateTime

abstract class Runtime {
    private val started = OffsetDateTime.now()

    abstract val serviceStorage: RuntimeServiceStorage<*>
    abstract val groupStorage: RuntimeGroupStorage
    abstract val factory: RuntimeFactory<AbstractService>
    abstract val expender: RuntimeExpender<AbstractService>
    abstract val templateStorage: RuntimeTemplateStorage<*, AbstractService>
    abstract val configHolder: RuntimeConfigHolder
    abstract val detectedLocalAddress: String
    abstract val serviceStatsThread: AbstractServiceStatsThread<*>

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