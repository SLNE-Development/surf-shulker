package dev.slne.surf.shulker.core.service

import dev.slne.surf.shulker.api.ShulkerApi
import dev.slne.surf.shulker.api.property.PropertyHolder
import dev.slne.surf.shulker.api.property.STATIC
import dev.slne.surf.shulker.api.service.Service
import dev.slne.surf.shulker.api.service.ServiceInformation
import dev.slne.surf.shulker.api.service.events.ServiceChangeStateEvent
import dev.slne.surf.shulker.api.template.Template
import dev.slne.surf.shulker.core.group.AbstractGroup
import dev.slne.surf.shulker.core.utils.IndexDetector
import dev.slne.surf.shulker.core.utils.PortDetector
import dev.slne.surf.shulker.proto.group.GroupType
import dev.slne.surf.shulker.proto.service.ServiceState
import java.nio.file.Path
import java.time.OffsetDateTime

abstract class AbstractService(
    groupName: String,
    id: Int,
    state: ServiceState,
    type: GroupType,
    properties: PropertyHolder,
    hostname: String,
    port: Int,
    templates: List<Template>,
    information: ServiceInformation,
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

    suspend fun group() = ShulkerApi.groupProvider.findByName(groupName)

    suspend fun init() {
        val group = group()

        if (group != null) {
            properties += group.properties.all().map { it.key to it.value.toString() }.toMap()
        }

        ShulkerApi.eventProvider.call(
            ServiceChangeStateEvent(
                this,
                ServiceState.UNRECOGNIZED,
                state
            )
        )
    }

    suspend fun shutdown(shutdownCleanUp: Boolean = true) {
        ShulkerApi.factory.shutdownApplication(this, shutdownCleanUp)
    }

    suspend fun executeCommand(command: String) {
        ShulkerApi.expender.executeCommand(this, command)
    }

    suspend fun readLogs(limit: Int = 100) = ShulkerApi.expender.readLogs(this, limit)

}