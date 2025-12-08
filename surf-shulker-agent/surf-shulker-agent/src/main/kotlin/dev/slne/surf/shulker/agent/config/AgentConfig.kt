package dev.slne.surf.shulker.agent.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class AgentConfig(
    val autoUpdate: Boolean = false,
    val port: Int = 8123,
    val maxConcurrentServiceStarts: Int = 3,
    val maxCachingProcesses: Int = 5,
    val maxCpuPercentageToStart: Double = 0.75
)