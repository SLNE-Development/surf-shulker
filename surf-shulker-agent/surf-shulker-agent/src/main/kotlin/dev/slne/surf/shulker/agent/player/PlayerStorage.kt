package dev.slne.surf.shulker.agent.player

import dev.slne.surf.shulker.api.player.SharedPlayerProvider
import dev.slne.surf.shulker.core.player.AbstractShulkerPlayer
import java.util.*

interface PlayerStorage : SharedPlayerProvider<AbstractShulkerPlayer> {
    fun registerPlayer(player: AbstractShulkerPlayer)
    fun unregisterPlayer(uuid: UUID)
}