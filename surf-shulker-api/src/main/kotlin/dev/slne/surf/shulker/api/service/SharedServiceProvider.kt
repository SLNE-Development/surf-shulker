package dev.slne.surf.shulker.api.service

import dev.slne.surf.shulker.api.group.Group
import dev.slne.surf.shulker.proto.group.GroupType
import dev.slne.surf.shulker.proto.service.ServiceSnapshot
import it.unimi.dsi.fastutil.objects.ObjectList

interface SharedServiceProvider<S : Service> {
    suspend fun findAll(): ObjectList<S>

    suspend fun findByName(name: String): S?

    suspend fun findByType(type: GroupType): ObjectList<S>

    suspend fun findByGroup(group: Group): ObjectList<S>

    suspend fun bootInstanceWithConfiguration(
        name: String,
        configuration: SharedBootConfig
    ): ServiceSnapshot

    suspend fun bootInstance(name: String) =
        bootInstanceWithConfiguration(name, SharedBootConfig.EMPTY)

    suspend fun shutdownService(service: Service): ServiceSnapshot
}