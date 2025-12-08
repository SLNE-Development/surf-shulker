package dev.slne.surf.shulker.agent.runtime

import dev.slne.surf.shulker.agent.service.AbstractService

interface RuntimeFactory<S : AbstractService> {
    fun bootApplication(service: @UnsafeVariance S)

    
}