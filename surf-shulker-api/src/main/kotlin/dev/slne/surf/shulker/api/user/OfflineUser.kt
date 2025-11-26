package dev.slne.surf.shulker.api.user

import dev.slne.surf.shulker.api.user.online.User
import java.util.*

interface OfflineUser {
    val uuid: UUID

    suspend fun onlinePlayer(): User
    suspend fun isOnline(): Boolean
}