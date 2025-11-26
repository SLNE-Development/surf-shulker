package dev.slne.surf.shulker.node.docker

import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.okhttp.OkDockerHttpClient
import dev.slne.surf.shulker.node.common.Node
import dev.slne.surf.shulker.node.common.container.Container
import dev.slne.surf.shulker.node.docker.config.DockerConfig
import dev.slne.surf.shulker.node.docker.console.Console
import dev.slne.surf.shulker.node.docker.container.DockerContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.Unmodifiable
import java.util.*

fun main(): Unit = runBlocking {
    val node = DockerNode(
        DockerConfig(
            host = "tcp://localhost:2375",
            registryConfig = DockerConfig.DockerRegistryConfig(
                url = "https://index.docker.io/v1/"
            )
        )
    )

    node.findRegisteredContainers()

    node.containers.forEach { container ->
        println("Found container: ${container.uuid} on port ${container.port}")
    }

    Console(node).start()
    awaitCancellation()
}

class DockerNode(
    val config: DockerConfig
) : Node {
    private val _containers = mutableListOf<Container>()
    override val containers: @Unmodifiable List<Container>
        get() = _containers.toList()

    override val dockerHost: String = config.host

    private val clientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
        .apply {
            withDockerHost(dockerHost)

            if (config.verifyTls) {
                withDockerTlsVerify(true)
                withDockerCertPath(config.dockerCertPath)
            }

            val registryConfig = config.registryConfig
            if (registryConfig != null) {
                withRegistryUsername(registryConfig.username)
                withRegistryPassword(registryConfig.password)
                withRegistryEmail(registryConfig.email)
                withRegistryUrl(registryConfig.url)
            }
        }.build()

    private val httpClient = OkDockerHttpClient.Builder()
        .dockerHost(clientConfig.dockerHost)
        .sslConfig(clientConfig.sslConfig)
        .readTimeout(config.readTimeoutMs.toInt())
        .readTimeout(config.readTimeoutMs.toInt())
        .build()

    private val dockerClient = DockerClientImpl.getInstance(clientConfig, httpClient)

    override suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        true
    }

    override suspend fun disconnect(): Boolean = withContext(Dispatchers.IO) {
        true
    }

    override suspend fun createContainer(
        uuid: UUID,
        port: Int,
        persistentVolumes: Boolean,
        memoryLimit: Long?,
        cpuLimit: Double?,
        cpuPinning: List<Int>
    ): Container = DockerContainer(
        dockerClient = dockerClient,
        uuid = uuid,
        port = port,
    )

    override suspend fun createVolume(name: String): Boolean = withContext(Dispatchers.IO) {
        dockerClient.createVolumeCmd()
            .withName(name)
            .exec()

        true
    }

    override suspend fun findRegisteredContainers() {
        val dockerContainers = dockerClient.listContainersCmd().exec()

        dockerContainers.forEach { raw ->
            println(
                "Docker Container: ${raw.id} - ${raw.names.joinToString(",")} - ${
                    raw.ports.joinToString(
                        ","
                    )
                }"
            )
        }

        _containers.addAll(dockerContainers.map {
            DockerContainer.containerFromDockerContainer(dockerClient, it)
        }.toList())
    }

    override fun findContainerByUuid(uuid: UUID): Container? {
        return containers.find { it.uuid == uuid }
    }
}