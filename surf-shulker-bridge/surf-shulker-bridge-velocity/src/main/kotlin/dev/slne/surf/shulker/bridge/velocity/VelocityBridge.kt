package dev.slne.surf.shulker.bridge.velocity

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.KickedFromServerEvent
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.RegisteredServer
import com.velocitypowered.api.proxy.server.ServerInfo
import dev.slne.surf.shulker.api.player.ShulkerPlayer
import dev.slne.surf.shulker.api.player.events.PlayerNetworkConnectEvent
import dev.slne.surf.shulker.api.player.events.PlayerNetworkDisconnectEvent
import dev.slne.surf.shulker.api.service.Service
import dev.slne.surf.shulker.bridge.api.ServerBridge
import kotlinx.coroutines.runBlocking
import org.gradle.api.plugins.PluginContainer
import java.net.InetSocketAddress
import java.util.*
import kotlin.jvm.optionals.getOrNull

class VelocityBridge @Inject constructor(
    val proxyServer: ProxyServer,
    suspendingPluginContainer: SuspendingPluginContainer,
    val pluginContainer: PluginContainer
) : ServerBridge<RegisteredServer, ServerInfo>() {
    init {
        suspendingPluginContainer.initialize(this)

        proxyServer.allServers.forEach {
            proxyServer.unregisterServer(it.serverInfo)
        }
    }

    @Subscribe
    fun onInitialize(event: ProxyInitializeEvent) {
        runBlocking {
            processBind()
        }
    }

    @Subscribe
    fun onPreConnect(event: PlayerChooseInitialServerEvent) {
        findFallback()?.let { event.setInitialServer(it) }
    }

    @Subscribe
    fun onServerConnect(event: ServerConnectedEvent) {
        val player = event.player
        updatePlayer(
            PlayerNetworkConnectEvent(
                ShulkerPlayer(
                    player.uniqueId,
                    player.username,
                    event.server.serverInfo.name
                )
            )
        )
    }

    @Subscribe
    fun onDisconnect(event: DisconnectEvent) {
        val player = event.player

        val serviceName = player.currentServer
            .flatMap { Optional.ofNullable(it.serverInfo.name) }
            .orElse(null)
        if (serviceName == null) {
            return
        }

        updatePlayer(
            PlayerNetworkDisconnectEvent(
                ShulkerPlayer(
                    player.uniqueId,
                    player.username,
                    serviceName
                )
            )
        )
    }

    @Subscribe
    fun onKick(event: KickedFromServerEvent) {
        if (event.player.isActive) {
            if (event.server.serverInfo == null) {
                return
            }

            val server = findFallback()
            if (server == null || server == event.server) {
                return
            }

            event.result = KickedFromServerEvent.RedirectPlayer.create(server)
        }
    }

    override fun generateServerInfo(service: Service) =
        ServerInfo(service.name, InetSocketAddress(service.hostname, service.port))

    override fun registerServerInfo(
        identifier: ServerInfo,
        service: Service
    ): RegisteredServer = proxyServer.registerServer(identifier)

    override fun unregister(identifier: RegisteredServer) =
        proxyServer.unregisterServer(identifier.serverInfo)

    override fun findServer(name: String) = proxyServer.getServer(name).getOrNull()
    override fun playerCount(info: RegisteredServer) = info.playersConnected.size
}
