package dev.slne.surf.shulker.agent.player

import dev.slne.surf.shulker.agent.Agent
import dev.slne.surf.shulker.api.event.subscribe
import dev.slne.surf.shulker.api.player.events.PlayerNetworkConnectEvent
import dev.slne.surf.shulker.api.player.events.PlayerNetworkDisconnectEvent
import dev.slne.surf.shulker.core.player.AbstractShulkerPlayer

object PlayerListener {
    init {
        Agent.eventProvider.subscribe<PlayerNetworkConnectEvent> { event ->
            val player = event.player
            val abstractPlayer = AbstractShulkerPlayer(
                name = player.name,
                uuid = player.uuid,
                currentServiceName = player.currentServiceName
            )

            Agent.playerProvider.registerPlayer(abstractPlayer)
        }

        Agent.eventProvider.subscribe<PlayerNetworkDisconnectEvent> { event ->
            Agent.playerProvider.unregisterPlayer(event.player.uuid)
        }
    }
}