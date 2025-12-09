package dev.slne.surf.shulker.agent.platform

import dev.slne.surf.shulker.agent.Agent
import dev.slne.surf.shulker.proto.platform.FindPlatformRequest
import dev.slne.surf.shulker.proto.platform.FindPlatformResponse
import dev.slne.surf.shulker.proto.platform.PlatformControllerGrpcKt
import dev.slne.surf.shulker.proto.platform.findPlatformResponse

object PlatformGrpcService : PlatformControllerGrpcKt.PlatformControllerCoroutineImplBase() {
    private val platformStorage = Agent.platformStorage

    override suspend fun find(request: FindPlatformRequest): FindPlatformResponse {
        return findPlatformResponse {
            this.platforms.addAll(
                if (request.hasName()) {
                platformStorage.findByName(request.name)?.let { listOf(it.toSnapshot()) }
                    ?: emptyList()
            } else if (request.hasType()) {
                platformStorage.findByType(request.type).map { it.toSnapshot() }
            } else {
                platformStorage.findAll().map { it.toSnapshot() }
            })
        }
    }
}