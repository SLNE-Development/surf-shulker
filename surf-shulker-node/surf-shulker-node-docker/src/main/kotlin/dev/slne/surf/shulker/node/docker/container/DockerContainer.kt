package dev.slne.surf.shulker.node.docker.container

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import dev.slne.surf.shulker.node.common.container.Container
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

private const val BASE_IMAGE = "ubuntu:24.04"

const val SHULKER_DOCKER_CONTAINER_LABEL_UUID = "surf.shulker.node.container.uuid"
const val SHULKER_DOCKER_CONTAINER_LABEL_PERSISTENT_VOLUMES =
    "surf.shulker.node.container.persistentVolumes"
const val SHULKER_DOCKER_CONTAINER_LABEL_PORT = "surf.shulker.node.container.port"

class DockerContainer(
    private val dockerClient: DockerClient,
    override val uuid: UUID,
    override val port: Int,
    override val memoryLimit: Long? = null,
    override val cpuLimit: Double? = null,
    override val cpuPinning: List<Int> = emptyList(),
    override val persistentVolumes: Boolean = false,
) : Container {
    private val bindingPort = Ports.Binding.bindPort(port)
    private val exposedPort = ExposedPort.tcp(port)
    private val portBinding = PortBinding(bindingPort, exposedPort)

    override var host: String = ""

    private lateinit var containerId: String

    private lateinit var persistentVolumeId: String
    private lateinit var runtimeVolumeId: String

    private val hostConfig = HostConfig().apply {
        withPortBindings(portBinding)
        withMemory(memoryLimit)

        if (cpuPinning.isNotEmpty()) {
            val cpuSet = cpuPinning.joinToString(separator = ",")
            withCpusetCpus(cpuSet)
        }

        if (cpuLimit != null && cpuLimit > 0) {
            val cpuQuota = (cpuLimit * 100000).toLong()
            withCpuQuota(cpuQuota)
        }
    }

    private val labels = mapOf(
        SHULKER_DOCKER_CONTAINER_LABEL_UUID to uuid.toString(),
        SHULKER_DOCKER_CONTAINER_LABEL_PERSISTENT_VOLUMES to persistentVolumes.toString(),
        SHULKER_DOCKER_CONTAINER_LABEL_PORT to port.toString()
    )

    override suspend fun create() = withContext(Dispatchers.IO) {
        if (::containerId.isInitialized) error("Container already initialized")

        dockerClient.pullImageCmd(BASE_IMAGE).start().awaitCompletion()

        val result = dockerClient.createContainerCmd(BASE_IMAGE)
            .withHostConfig(hostConfig)
            .withName(uuid.toString())
            .withLabels(labels)
            .exec()

        containerId = result.id

        host = dockerClient.inspectContainerCmd(containerId)
            .exec()
            .networkSettings
            .ports
            .bindings[exposedPort]
            ?.firstOrNull()
            ?.hostIp ?: "localhost"

        if (persistentVolumes) {
            persistentVolumeId = dockerClient.createVolumeCmd()
                .withName("${uuid}_persistent")
                .exec()
                .name
        }

        runtimeVolumeId = dockerClient.createVolumeCmd()
            .withName("${uuid}_runtime")
            .exec()
            .name
    }

    override suspend fun start() = withContext(Dispatchers.IO) {
        if (!::containerId.isInitialized) error("Container not initialized")

        dockerClient.startContainerCmd(containerId).exec()

        Unit
    }

    override suspend fun stop() = withContext(Dispatchers.IO) {
        if (!::containerId.isInitialized) error("Container not initialized")

        dockerClient.stopContainerCmd(containerId).exec()

        Unit
    }

    override suspend fun kill() = withContext(Dispatchers.IO) {
        if (!::containerId.isInitialized) error("Container not initialized")

        dockerClient.killContainerCmd(containerId).exec()

        Unit
    }

    override suspend fun destroy() = withContext(Dispatchers.IO) {
        if (!::containerId.isInitialized) error("Container not initialized")

        dockerClient.removeContainerCmd(containerId).exec()

        if (persistentVolumes) {
            dockerClient.removeVolumeCmd(persistentVolumeId).exec()
        }

        dockerClient.removeVolumeCmd(runtimeVolumeId).exec()

        Unit
    }

    companion object {
        fun containerFromDockerContainer(
            dockerClient: DockerClient,
            container: com.github.dockerjava.api.model.Container
        ): DockerContainer {
            val labels = container.labels

            val uuid = UUID.fromString(labels[SHULKER_DOCKER_CONTAINER_LABEL_UUID])
            val persistentVolumes =
                labels[SHULKER_DOCKER_CONTAINER_LABEL_PERSISTENT_VOLUMES]?.toBoolean()
                    ?: false
            val port =
                labels[SHULKER_DOCKER_CONTAINER_LABEL_PORT]?.toInt() ?: error("Port label missing")

            return DockerContainer(
                dockerClient = dockerClient,
                uuid = uuid,
                port = port,
                persistentVolumes = persistentVolumes,
            )
        }
    }
}