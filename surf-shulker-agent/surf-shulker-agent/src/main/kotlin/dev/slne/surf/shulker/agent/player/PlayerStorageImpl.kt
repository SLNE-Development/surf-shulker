package dev.slne.surf.shulker.agent.player

import com.github.benmanes.caffeine.cache.Caffeine
import dev.slne.surf.shulker.core.player.AbstractShulkerPlayer
import dev.slne.surf.surfapi.core.api.util.toObjectList
import it.unimi.dsi.fastutil.objects.ObjectList
import java.util.*

object PlayerStorageImpl : PlayerStorage {
    private val cache = Caffeine.newBuilder().build<UUID, AbstractShulkerPlayer>()

    override fun registerPlayer(player: AbstractShulkerPlayer) {
        cache.put(player.uuid, player)
    }

    override fun unregisterPlayer(uuid: UUID) {
        cache.invalidate(uuid)
    }

    override suspend fun findAll(): ObjectList<AbstractShulkerPlayer> {
        return cache.asMap().values.toObjectList()
    }

    override suspend fun findByName(name: String): AbstractShulkerPlayer? {
        return cache.asMap().values.firstOrNull { it.name.equals(name, ignoreCase = true) }
    }

    override suspend fun findByServiceName(serviceName: String): ObjectList<AbstractShulkerPlayer> {
        return cache.asMap().values
            .filter { it.currentServiceName == serviceName }
            .toObjectList()
    }

    override suspend fun playerCount(): Int {
        return cache.asMap().size
    }
}