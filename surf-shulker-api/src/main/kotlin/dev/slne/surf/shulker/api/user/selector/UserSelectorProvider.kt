package dev.slne.surf.shulker.api.user.selector

import dev.slne.surf.surfapi.core.api.util.requiredService

private val provider = requiredService<UserSelectorProvider>()

interface UserSelectorProvider {
    fun createSelector(): UserSelector

    companion object : UserSelectorProvider by provider {
        val INSTANCE get() = provider

        operator fun invoke(): UserSelector = provider.createSelector()
    }
}