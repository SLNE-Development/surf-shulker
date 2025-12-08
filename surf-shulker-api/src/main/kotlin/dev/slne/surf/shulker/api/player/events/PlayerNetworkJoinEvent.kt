package dev.slne.surf.shulker.api.player.events

import dev.slne.surf.shulker.api.event.Event
import dev.slne.surf.shulker.api.player.ShulkerPlayer
import kotlinx.serialization.Serializable

@Serializable
class PlayerNetworkJoinEvent(
    val player: ShulkerPlayer
) : Event 