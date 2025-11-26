package dev.slne.surf.shulker.node.docker.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Setting
import kotlin.time.Duration.Companion.seconds

@ConfigSerializable
data class DockerConfig(
    val host: String = "localhost",
    val verifyTls: Boolean = false,
    val dockerCertPath: String = "",

    val readTimeoutMs: Long = 30.seconds.inWholeMilliseconds,
    val responseTimeoutMs: Long = 45.seconds.inWholeMilliseconds,

    @Setting("registry")
    val registryConfig: DockerRegistryConfig? = null
) {
    @ConfigSerializable
    data class DockerRegistryConfig(
        val username: String = "",
        val password: String = "",
        val email: String = "",
        val url: String = ""
    )
}