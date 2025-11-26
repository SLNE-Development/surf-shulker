package dev.slne.surf.shulker.api.server.utils

import dev.slne.surf.shulker.api.server.online.Server
import dev.slne.surf.shulker.api.utils.intent.Intent

interface Sendable {
    suspend fun pull(): SendResult
    suspend fun pullWithIntent(vararg intent: Intent): SendResult

    suspend fun push(server: Server): SendResult
    suspend fun pushWithIntent(server: Server, vararg intent: Intent): SendResult
}