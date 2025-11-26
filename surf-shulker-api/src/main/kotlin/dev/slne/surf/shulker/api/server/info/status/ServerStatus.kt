package dev.slne.surf.shulker.api.server.info.status

import kotlinx.serialization.Serializable

@Serializable
enum class ServerStatus(
    val joinable: Boolean,
    val bypassable: Boolean,
) {
    OFFLINE(false, false),
    STARTING(false, false),
    LOBBY(true, true),
    IN_GAME(false, true),
    PERSISTENT(true, true),
    RESTARTING(false, false),
    MAINTENANCE(false, true);
}