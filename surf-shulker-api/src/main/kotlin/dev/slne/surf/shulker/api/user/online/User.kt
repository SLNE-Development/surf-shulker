package dev.slne.surf.shulker.api.user.online

import dev.slne.surf.shulker.api.server.utils.Sendable
import dev.slne.surf.shulker.api.user.OfflineUser
import net.kyori.adventure.audience.Audience

interface User : OfflineUser, Audience, Sendable {
    val offlinePlayer: OfflineUser
}