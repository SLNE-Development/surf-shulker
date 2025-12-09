package dev.slne.surf.shulker.runtime.common

import dev.slne.surf.shulker.api.group.Group
import dev.slne.surf.shulker.api.service.Service
import dev.slne.surf.shulker.proto.service.ServiceSnapshot

interface RuntimeFactory<S : Service> {
    suspend fun bootApplication(service: @UnsafeVariance S)

    suspend fun shutdownApplication(
        service: @UnsafeVariance S,
        shutdownCleanup: Boolean = true
    ): ServiceSnapshot

    fun generateInstance(group: Group): S

}