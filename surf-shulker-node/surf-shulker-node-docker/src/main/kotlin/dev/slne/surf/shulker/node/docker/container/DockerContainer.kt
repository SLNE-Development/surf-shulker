package dev.slne.surf.shulker.node.docker.container

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.model.*
import dev.slne.surf.shulker.node.common.container.Container
import dev.slne.surf.shulker.node.docker.DockerNode
import dev.slne.surf.shulker.node.docker.utils.DockerLabel
import dev.slne.surf.shulker.node.docker.utils.SurfLabel
import dev.slne.surf.shulker.node.docker.utils.pullImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

//private const val BASE_IMAGE = "eclipse-temurin:24-jre-alpine"
private const val BASE_IMAGE = "ubuntu:24.04"

class DockerContainer(
    private val node: DockerNode,
    private val dockerClient: DockerClient,
    override val uuid: UUID,
    override val port: Int,
    override val memoryLimit: Long? = null,
    override val cpuLimit: Double? = null,
    override val cpuPinning: List<Int> = emptyList(),
    override val persistentVolumes: Boolean = false,
    containerId: String? = null
) : Container {
    private val bindingPort = Ports.Binding.bindPort(port)
    private val exposedPort = ExposedPort.tcp(port)
    private val portBinding = PortBinding(bindingPort, exposedPort)

    override var host: String = ""

    private lateinit var containerId: String

    private val persistentVolumeId: String = "${uuid}_persistent"
    private val runtimeVolumeId: String = "${uuid}_runtime"

    private val persistentVolume = Bind(persistentVolumeId, Volume("/data/persistent"))
    private val runtimeVolume = Bind(runtimeVolumeId, Volume("/data/runtime"))
    private val binds = if (persistentVolumes) {
        listOf(persistentVolume, runtimeVolume)
    } else {
        listOf(runtimeVolume)
    }

    init {
        if (containerId != null) {
            this.containerId = containerId
        }
    }

    private val hostConfig = HostConfig().apply {
        withPortBindings(portBinding)
        withMemory(memoryLimit)
        withOomKillDisable(false)
        withBinds(this@DockerContainer.binds)

        if (cpuPinning.isNotEmpty()) {
            val cpuSet = cpuPinning.joinToString(separator = ",")
            withCpusetCpus(cpuSet)
        }

        if (cpuLimit != null && cpuLimit > 0) {
            val cpuQuota = (cpuLimit * 100000).toLong()
            withCpuQuota(cpuQuota)
        }
    }

    private fun createVolumes() {
        if (persistentVolumes) {
            dockerClient.createVolumeCmd()
                .withName("${uuid}_persistent")
                .exec()
        }

        dockerClient.createVolumeCmd()
            .withName("${uuid}_runtime")
            .exec()
    }

    private fun connectNetwork() {
        dockerClient.connectToNetworkCmd()
            .withContainerId(containerId)
            .withNetworkId(node.networkId)
            .exec()
    }

    override suspend fun create(): Unit = withContext(Dispatchers.IO) {
        if (::containerId.isInitialized) error("Container already initialized")

        dockerClient.pullImage(BASE_IMAGE)

        createVolumes()

        val createCommand = dockerClient.createContainerCmd(BASE_IMAGE)
            .withHostConfig(hostConfig)
            .withName(uuid.toString())
            .withLabels(SurfLabel.toMap(this@DockerContainer))
            .withCmd("tail", "-f", "/dev/null")

        if (persistentVolumes) {
            createCommand.withVolumes(listOf(persistentVolume.volume, runtimeVolume.volume))
        } else {
            createCommand.withVolumes(listOf(runtimeVolume.volume))
        }

        val result = createCommand.exec()

        containerId = result.id

        host = dockerClient.inspectContainerCmd(containerId)
            .exec()
            .networkSettings
            .ports
            .bindings[exposedPort]
            ?.firstOrNull()
            ?.hostIp ?: "localhost"

        connectNetwork()
    }

    override suspend fun start(): Unit = withContext(Dispatchers.IO) {
        if (!::containerId.isInitialized) error("Container not initialized")

        dockerClient.startContainerCmd(containerId).exec()
    }

    override suspend fun stop(): Unit = withContext(Dispatchers.IO) {
        if (!::containerId.isInitialized) error("Container not initialized")

        dockerClient.stopContainerCmd(containerId).exec()
    }

    override suspend fun kill(): Unit = withContext(Dispatchers.IO) {
        if (!::containerId.isInitialized) error("Container not initialized")

        dockerClient.killContainerCmd(containerId).exec()
    }

    override suspend fun destroy(): Unit = withContext(Dispatchers.IO) {
        if (!::containerId.isInitialized) error("Container not initialized")

        dockerClient.removeContainerCmd(containerId).exec()
        dockerClient.removeVolumeCmd(runtimeVolumeId).exec()
    }

    companion object {
        fun containerFromDockerContainer(
            node: DockerNode,
            dockerClient: DockerClient,
            container: com.github.dockerjava.api.model.Container
        ): DockerContainer {
            val labels = SurfLabel.fromDockerContainer(container)

            val uuidLabel = labels[SurfLabel.UUID_LABEL] as? DockerLabel.UuidDockerLabel
            val portLabel = labels[SurfLabel.PORT_LABEL] as? DockerLabel.IntDockerLabel
            val persistentVolumesLabel =
                labels[SurfLabel.PERSISTENT_VOLUMES_LABEL] as? DockerLabel.BooleanDockerLabel

            val uuid = uuidLabel?.value
                ?: error("Container ${container.id} is missing UUID label")
            val port = portLabel?.value
                ?: error("Container ${container.id} is missing port label")
            val persistentVolumes = persistentVolumesLabel?.value
                ?: error("Container ${container.id} is missing persistent volumes label")

            return DockerContainer(
                node = node,
                dockerClient = dockerClient,
                uuid = uuid,
                port = port,
                persistentVolumes = persistentVolumes,
                containerId = container.id
            )
        }
    }
}