package dev.slne.surf.shulker.core.utils

import dev.slne.surf.shulker.api.ShulkerApi
import dev.slne.surf.shulker.api.property.START_PORT
import dev.slne.surf.shulker.core.group.AbstractGroup
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress
import java.net.ServerSocket

object PortDetector {
    // TODO: Fix blocking
    fun nextAvailablePort(group: AbstractGroup): Int = runBlocking {
        var port = group.platform.defaultStartPort ?: 30000
        val startPortProperty = group.properties[START_PORT]

        if (startPortProperty != null) {
            try {
                port = startPortProperty
            } catch (_: NumberFormatException) {
                // Ignore invalid port property
            }
        }

        while (isPortUsed(port)) {
            port++
        }

        return@runBlocking port
    }

    private suspend fun isPortUsed(port: Int): Boolean {
        ShulkerApi.serviceProvider.findAll().forEach { service ->
            if (service.port == port) {
                return true
            }
        }

        try {
            ServerSocket().use { socket ->
                socket.bind(InetSocketAddress(port))
                return false
            }
        } catch (_: Exception) {
            return true
        }
    }
}