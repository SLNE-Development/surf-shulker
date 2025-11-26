package dev.slne.surf.shulker.api.user.selector

import dev.slne.surf.shulker.api.user.OfflineUser
import it.unimi.dsi.fastutil.objects.ObjectList
import java.util.*

interface UserSelector {
    fun by(uuid: UUID): UserSelector
    fun by(name: String): UserSelector

    fun keepInMemory(): UserSelector = keepInMemory(true)
    fun keepInMemory(keep: Boolean): UserSelector

    fun fetchPlayerProfile(): UserSelector = fetchPlayerProfile(true)
    fun fetchPlayerProfile(fetch: Boolean): UserSelector

    fun clear(): UserSelector

    suspend fun find(): OfflineUser?
    suspend fun findAll(): ObjectList<OfflineUser>

    companion object : UserSelector by UserSelector() {
        operator fun invoke(): UserSelector = UserSelectorProvider.createSelector()
    }
}