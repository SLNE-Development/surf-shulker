package dev.slne.surf.shulker.api.server

import dev.slne.surf.shulker.api.server.info.ServerInfo
import dev.slne.surf.shulker.api.server.info.status.ServerStatus
import dev.slne.surf.shulker.api.server.online.Server
import java.util.*

interface OfflineServer {
    val uuid: UUID
    val info: ServerInfo
    val status: ServerStatus

    val name get() = info.name
    val group get() = info.group

    suspend fun onlineServer(): Server
}