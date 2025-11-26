package dev.slne.surf.shulker.api.server.selector

import dev.slne.surf.surfapi.core.api.util.requiredService

private val provider = requiredService<ServerSelectorProvider>()

interface ServerSelectorProvider {
    fun createSelector(): ServerSelector

    companion object : ServerSelectorProvider by provider {
        val INSTANCE get() = provider

        operator fun invoke(): ServerSelector = provider.createSelector()
    }
}