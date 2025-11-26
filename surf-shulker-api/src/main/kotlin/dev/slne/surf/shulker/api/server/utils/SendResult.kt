package dev.slne.surf.shulker.api.server.utils

import kotlinx.serialization.Serializable

@Serializable
enum class SendResult {
    SUCCESS,

    SERVER_FULL,
    CONNECTION_ERROR,
    UNKNOWN_ERROR
}