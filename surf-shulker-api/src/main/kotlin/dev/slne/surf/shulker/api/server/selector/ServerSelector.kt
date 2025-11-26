package dev.slne.surf.shulker.api.server.selector

import dev.slne.surf.shulker.api.server.OfflineServer
import dev.slne.surf.shulker.api.server.group.ServerGroup
import it.unimi.dsi.fastutil.objects.ObjectList
import java.util.*

interface ServerSelector {
    fun by(uuid: UUID): ServerSelector
    fun by(group: ServerGroup): ServerSelector

    fun clear(): ServerSelector

    fun keepInMemory(): ServerSelector = keepInMemory(true)
    fun keepInMemory(keep: Boolean): ServerSelector

    suspend fun find(): OfflineServer?
    suspend fun findAll(): ObjectList<OfflineServer>

    companion object : ServerSelector by ServerSelector() {
        operator fun invoke(): ServerSelector = ServerSelectorProvider.createSelector()
    }
}