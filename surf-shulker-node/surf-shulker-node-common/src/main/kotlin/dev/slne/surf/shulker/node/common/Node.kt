package dev.slne.surf.shulker.node.common

import dev.slne.surf.shulker.node.common.container.Container
import org.jetbrains.annotations.Unmodifiable
import java.util.*

interface Node {
    val dockerHost: String

    val containers: @Unmodifiable List<Container>

    suspend fun connect(): Boolean
    suspend fun disconnect(): Boolean

    fun findContainerByUuid(uuid: UUID): Container?
    suspend fun findRegisteredContainers()

    suspend fun createContainer(
        uuid: UUID,
        port: Int,
        persistentVolumes: Boolean = false,
        memoryLimit: Long? = null,
        cpuLimit: Double? = null,
        cpuPinning: List<Int> = emptyList()
    ): Container

    suspend fun createVolume(
        name: String
    ): Boolean
}