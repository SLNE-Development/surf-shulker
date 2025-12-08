package dev.slne.surf.shulker.agent.runtime

import dev.slne.surf.shulker.agent.group.AbstractGroup
import dev.slne.surf.shulker.agent.service.AbstractService
import dev.slne.surf.shulker.proto.service.ServiceSnapshot

interface RuntimeFactory<S : AbstractService> {
    suspend fun bootApplication(service: @UnsafeVariance S)

    suspend fun shutdownApplication(
        service: @UnsafeVariance S,
        shutdownCleanup: Boolean = true
    ): ServiceSnapshot

    fun generateInstance(group: AbstractGroup): S

}