package dev.slne.surf.shulker.core.service

import dev.slne.surf.shulker.api.property.STATIC
import dev.slne.surf.shulker.api.service.Service
import dev.slne.surf.shulker.api.service.ServiceInformation
import dev.slne.surf.shulker.api.service.events.ServiceChangePlayerCountEvent
import dev.slne.surf.shulker.api.service.events.ServiceChangeStateEvent
import dev.slne.surf.shulker.api.template.Template
import dev.slne.surf.shulker.core.group.AbstractGroup
import dev.slne.surf.shulker.proto.group.GroupType
import dev.slne.surf.shulker.proto.service.ServiceState
import java.nio.file.Path
import java.time.OffsetDateTime

abstract class AbstractService(
    groupName: String,
    id: Int,
    state: ServiceState,
    type: GroupType,
    properties: Map<String, String>,
    hostname: String,
    port: Int,
    templates: List<Template>,
    information: ServiceInformation,
    minMemory: Int,
    maxMemory: Int,
    playerCount: Int = -1,
    maxPlayerCount: Int = -1,
    memoryUsage: Double = -1.0,
    cpuUsage: Double = -1.0,
    motd: String = ""
) : Service(
    groupName = groupName,
    id = id,
    state = state,
    type = type,
    properties = properties,
    _hostname = hostname,
    port = port,
    templates = templates,
    information = information,
    _minMemory = minMemory,
    _maxMemory = maxMemory,
    _playerCount = playerCount,
    _maxPlayerCount = maxPlayerCount,
    _memoryUsage = memoryUsage,
    _cpuUsage = cpuUsage,
    _motd = motd
) {
    val path: Path =
        (if (isStatic) LOCAL_STATIC_FACTORY_PATH else LOCAL_FACTORY_PATH).resolve(name)

    val isStatic get() = properties[STATIC.name]?.toBoolean() ?: false

    constructor(group: AbstractGroup) : this(
        groupName = group.name,
        id = IndexDetector.findIndex(group),
        state = ServiceState.PREPARING,
        type = group.platform.type,
        properties = group.properties.all().map { it.key to it.value.toString() }.toMap(),
        hostname = if (group.isProxy) "0.0.0.0" else "127.0.0.1",
        port = PortDetector.nextAvailablePort(group),
        templates = group.templates,
        information = ServiceInformation(OffsetDateTime.now()),
        minMemory = group.minMemory,
        maxMemory = group.maxMemory
    )

    suspend fun group() = Agent.runtime.groupStorage.findByName(groupName)

    suspend fun init() {
        val group = group()
        if (group != null) {
            properties += group.properties.all().map { it.key to it.value.toString() }.toMap()
        }

        Agent.eventProvider.call(ServiceChangeStateEvent(this, ServiceState.UNRECOGNIZED, state))
    }

    suspend fun shutdown(shutdownCleanUp: Boolean = true) {
        Agent.runtime.factory.shutdownApplication(this, shutdownCleanUp)
    }

    suspend fun executeCommand(command: String) {
        Agent.runtime.expender.executeCommand(this, command)
    }

    suspend fun readLogs(limit: Int = 100) = Agent.runtime.expender.readLogs(this, limit)

    fun updateMinMemory(memory: Int) {
        if (state == ServiceState.STARTING || state == ServiceState.ONLINE) {
            throw IllegalStateException("Cannot update minMemory while service is starting or online.")
        }

        this.minMemory = memory
    }

    fun updateMaxMemory(memory: Int) {
        if (state == ServiceState.STARTING || state == ServiceState.ONLINE) {
            throw IllegalStateException("Cannot update maxMemory while service is starting or online.")
        }

        this.maxMemory = memory
    }

    fun updateCpuUsage(usage: Double) {
        this.cpuUsage = usage
    }

    fun updateMaxPlayerCount(count: Int) {
        this.maxPlayerCount = count
    }

    suspend fun updatePlayerCount(count: Int) {
        val oldPlayerCount = this.playerCount
        this.playerCount = count

        if (this.state == ServiceState.ONLINE && oldPlayerCount != count) {
            Agent.eventProvider.call(
                ServiceChangePlayerCountEvent(this, oldPlayerCount, count)
            )
        }
    }

    fun updateMotd(motd: String) {
        this.motd = motd
    }

    fun updateMemoryUsage(usage: Double) {
        this.memoryUsage = usage
    }
}