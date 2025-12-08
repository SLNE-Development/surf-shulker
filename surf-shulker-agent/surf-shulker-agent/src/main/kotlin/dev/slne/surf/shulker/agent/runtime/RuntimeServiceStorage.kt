package dev.slne.surf.shulker.agent.runtime

import dev.slne.surf.shulker.agent.service.AbstractService
import dev.slne.surf.shulker.api.service.SharedServiceProvider

interface RuntimeServiceStorage<S : AbstractService> : SharedServiceProvider<S> {
    suspend fun deployService(service: S)

    suspend fun deployAbstractService(abstractService: AbstractService) {
        deployService(implementedService(abstractService))
    }

    suspend fun dropService(service: S)

    suspend fun dropAbstractService(abstractService: AbstractService) {
        dropService(implementedService(abstractService))
    }

    fun implementedService(abstractService: AbstractService): S
}