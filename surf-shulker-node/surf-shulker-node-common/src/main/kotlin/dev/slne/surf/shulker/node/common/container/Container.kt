package dev.slne.surf.shulker.node.common.container

import java.util.*

interface Container {
    val uuid: UUID

    val host: String
    val port: Int

    val cpuPinning: List<Int>
    val cpuLimit: Double?
    val memoryLimit: Long?

    val persistentVolumes: Boolean

    suspend fun start()
    suspend fun stop()
    suspend fun kill()

    suspend fun create()
    suspend fun destroy()
}