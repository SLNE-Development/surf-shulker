package dev.slne.surf.shulker.api.server.info

import dev.slne.surf.shulker.api.server.group.ServerGroup
import kotlinx.serialization.Serializable

@Serializable
data class ServerInfo(
    val name: String,
    val group: ServerGroup,

    val currentPlayers: Int,
    val maxPlayers: Int
)