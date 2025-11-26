package dev.slne.surf.shulker.api.server.group

import dev.slne.surf.shulker.api.server.OfflineServer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ServerGroup(
    val uuid: @Contextual UUID,
    val name: String,
    val servers: List<OfflineServer>
)