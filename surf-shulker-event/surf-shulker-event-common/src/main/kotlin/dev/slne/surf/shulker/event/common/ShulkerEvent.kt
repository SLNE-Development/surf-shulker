package dev.slne.surf.shulker.event.common

abstract class ShulkerEvent(source: Any) {
    suspend fun post() {

    }

    suspend fun postAndForget() {

    }
}