package dev.slne.surf.shulker.runtime.common

import dev.slne.surf.shulker.api.service.Service
import dev.slne.surf.shulker.api.service.SharedServiceProvider

interface RuntimeServiceStorage<S : Service> : SharedServiceProvider<S> {
    suspend fun deployService(service: S)
    suspend fun dropService(service: S)

    fun implementedService(service: Service): S
}