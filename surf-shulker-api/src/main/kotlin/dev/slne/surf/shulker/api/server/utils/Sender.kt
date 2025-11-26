package dev.slne.surf.shulker.api.server.utils

import dev.slne.surf.shulker.api.server.online.Server
import dev.slne.surf.shulker.api.utils.intent.Intent

interface Sender {
    suspend fun pull(sendable: Sendable): SendResult
    suspend fun pullWithIntent(
        sendable: Sendable,
        vararg intent: Intent
    ): SendResult

    suspend fun push(sendable: Sendable, server: Server): SendResult
    suspend fun pushWithIntent(
        sendable: Sendable,
        server: Server,
        vararg intent: Intent
    ): SendResult
}