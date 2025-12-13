package dev.slne.surf.shulker.bridge.api

import dev.slne.surf.shulker.agent.Agent
import dev.slne.surf.shulker.api.event.Event
import dev.slne.surf.shulker.api.service.Service
import dev.slne.surf.shulker.api.service.events.ServiceChangeStateEvent
import dev.slne.surf.shulker.proto.group.GroupType
import dev.slne.surf.shulker.proto.service.ServiceState
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf

abstract class ServerBridge<S, I>(protected val agent: Agent = Agent) {
    val fallbacks = mutableObject2ObjectMapOf<Service, S>()

    suspend fun processBind() {
        agent.serviceProvider
            .findByType(GroupType.SERVER)
            .filter { it.state == ServiceState.ONLINE }
            .forEach { registerNewServer(it) }

        agent.eventProvider.subscribe(ServiceChangeStateEvent::class) {
            if (it.service.type == GroupType.SERVER) {
                handleServiceStateChange(it.service)
            }
        }
    }

    private fun handleServiceStateChange(service: Service) {
        when (service.state) {
            ServiceState.ONLINE -> if (service.type == GroupType.SERVER) registerNewServer(service)
            ServiceState.STOPPING -> unregisterServer(service)
            else -> {}
        }
    }

    protected open fun registerNewServer(service: Service) {
        val serverInfo = registerServerInfo(generateServerInfo(service), service)

        if (isFallback(service)) {
            fallbacks[service] = serverInfo
        }
    }

    private fun unregisterServer(service: Service) {
        findServer(service.name)?.let { server ->
            unregister(server)
        }
        fallbacks.remove(service)
    }

    fun updatePlayer(event: Event) = agent.eventProvider.call(event)
    fun hasFallbacks() = fallbacks.isNotEmpty()

    protected fun isFallback(service: Service) =
        service.properties["fallback"]?.equals("true", ignoreCase = true) == true

    fun findFallback() = fallbacks.keys
        .filter { findServer(it.name) != null }
        .sortedWith(
            compareBy(
                { it.properties["fallbackPriority"]?.toIntOrNull() ?: Int.MAX_VALUE },
                {
                    playerCount(
                        findServer(it.name) ?: error("Could not find server ${it.name}")
                    )
                }
            ))
        .firstOrNull()
        ?.let { fallbacks[it] }

    abstract fun generateServerInfo(service: Service): I
    abstract fun registerServerInfo(identifier: I, service: Service): S
    abstract fun unregister(identifier: S)
    abstract fun findServer(name: String): S?
    abstract fun playerCount(info: S): Int
}