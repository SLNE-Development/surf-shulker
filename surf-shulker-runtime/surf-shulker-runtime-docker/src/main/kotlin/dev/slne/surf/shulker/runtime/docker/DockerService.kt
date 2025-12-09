package dev.slne.surf.shulker.runtime.docker

import dev.slne.surf.shulker.agent.group.AbstractGroup
import dev.slne.surf.shulker.agent.service.AbstractService
import dev.slne.surf.shulker.agent.utils.IndexDetector
import dev.slne.surf.shulker.agent.utils.PortDetector
import dev.slne.surf.shulker.api.service.ServiceInformation
import dev.slne.surf.shulker.api.template.Template
import dev.slne.surf.shulker.proto.group.GroupType
import dev.slne.surf.shulker.proto.service.ServiceState
import java.time.OffsetDateTime

class DockerService(
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
) : AbstractService(
    groupName,
    id,
    state,
    type,
    properties,
    hostname,
    port,
    templates,
    information,
    minMemory,
    maxMemory,
    playerCount,
    maxPlayerCount,
    memoryUsage,
    cpuUsage,
    motd
) {
    var containerId: String? = null

    constructor(group: AbstractGroup) : this(
        group.name,
        IndexDetector.findIndex(group),
        ServiceState.PREPARING,
        group.platform.type,
        hashMapOf(),
        if (group.isProxy) "0.0.0.0" else "127.0.0.1",
        PortDetector.nextAvailablePort(group),
        group.templates,
        ServiceInformation(OffsetDateTime.now()),
        group.minMemory,
        group.maxMemory
    )

    fun changeToContainerHostname(hostname: String) {
        this.hostname = hostname
    }
}