package dev.slne.surf.shulker.agent.player

import dev.slne.surf.shulker.agent.Agent
import dev.slne.surf.shulker.api.event.subscribe
import dev.slne.surf.shulker.api.player.events.PlayerNetworkJoinEvent
import dev.slne.surf.shulker.api.player.events.PlayerNetworkQuitEvent
import dev.slne.surf.shulker.core.player.AbstractShulkerPlayer

object PlayerListener {
    init {
        Agent.eventProvider.subscribe<PlayerNetworkJoinEvent> { event ->
            val player = event.player
            val abstractPlayer = AbstractShulkerPlayer(
                name = player.name,
                uuid = player.uuid,
                currentServiceName = player.currentServiceName
            )

            Agent.playerProvider.registerPlayer(abstractPlayer)
        }

        Agent.eventProvider.subscribe<PlayerNetworkQuitEvent> { event ->
            Agent.playerProvider.unregisterPlayer(event.player.uuid)
        }
    }
}