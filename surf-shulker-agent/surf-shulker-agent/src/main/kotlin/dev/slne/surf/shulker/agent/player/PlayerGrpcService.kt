package dev.slne.surf.shulker.agent.player

import com.google.protobuf.Empty
import dev.slne.surf.shulker.agent.Agent
import dev.slne.surf.shulker.proto.player.*

object PlayerGrpcService : PlayerControllerGrpcKt.PlayerControllerCoroutineImplBase() {
    private val playerStorage get() = Agent.playerProvider

    override suspend fun findAll(request: Empty): FindPlayerResponse {
        return findPlayerResponse {
            this.players.addAll(playerStorage.findAll().map { it.toSnapshot() })
        }
    }

    override suspend fun findByName(request: FindPlayerByNameRequest): FindPlayerResponse {
        return findPlayerResponse {
            this.players.addAll(
                if (request.name.isNotEmpty()) {
                    playerStorage.findByName(request.name)?.let { listOf(it.toSnapshot()) }
                        ?: emptyList()
                } else {
                    playerStorage.findAll().map { it.toSnapshot() }
                })
        }
    }

    override suspend fun findByService(request: FindPlayerByServiceRequest): FindPlayerResponse {
        return findPlayerResponse {
            this.players.addAll(
                if (request.serviceName.isNotEmpty()) {
                    playerStorage.findByServiceName(request.serviceName).map { it.toSnapshot() }
                } else {
                    playerStorage.findAll().map { it.toSnapshot() }
                })
        }
    }

    override suspend fun count(request: PlayerCountRequest): PlayerCountResponse {
        return playerCountResponse {
            count = playerStorage.playerCount()
        }
    }
}