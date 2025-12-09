package dev.slne.surf.shulker.runtime.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.exception.NotModifiedException
import com.github.dockerjava.api.model.*
import dev.slne.surf.shulker.agent.group.AbstractGroup
import dev.slne.surf.shulker.agent.runtime.abstract.AbstractRuntimeFactory
import dev.slne.surf.shulker.proto.group.GroupType
import dev.slne.surf.surfapi.core.api.util.toMutableObjectList
import kotlinx.io.files.Path
import java.nio.file.Files
import java.nio.file.Paths

private const val RUNTIME_IMAGE = "openjdk:21-jdk"

class DockerRuntimeFactory(
    val client: DockerClient
) : AbstractRuntimeFactory<DockerService>(Path("local/temp")) {
    override suspend fun runRuntimeBoot(service: DockerService) {
        client.pullImageCmd(RUNTIME_IMAGE).exec(null)

        val createCmd = client.createContainerCmd(RUNTIME_IMAGE)
            .withName(service.name)
            .withWorkingDir("/app")
            .withCmd(*languageSpecificBootArguments(service).toTypedArray())

        val envList = environment(service).parameters.map { (key, value) -> "$key=$value" }
            .toMutableObjectList()
        if (envList.isNotEmpty()) {
            createCmd.withEnv(*envList.toTypedArray())
        }

        val bindSource = localUserVolumePath() + "\\temp\\${service.name}"
        val hostConfig = HostConfig.newHostConfig()
            .withAutoRemove(true)
            .withBinds(Bind.parse("$bindSource:/app"))
            .withMemory(service.maxMemory * 1024 * 1024L)

        if (service.type == GroupType.PROXY) {
            val exposed = ExposedPort.tcp(service.port)

            createCmd.withExposedPorts(exposed)
            hostConfig.withPortBindings(PortBinding(Ports.Binding.bindPort(service.port), exposed))
        }

        createCmd.withHostConfig(hostConfig)

        val containerResponse = createCmd.exec()
        client.startContainerCmd(containerResponse.id).exec()

        val inspection = client.inspectContainerCmd(containerResponse.id).exec()
        val ip = inspection.networkSettings.networks.values.firstOrNull()?.ipAddress
            ?: throw IllegalStateException("Failed to get container IP address")

        service.containerId = containerResponse.id
        service.changeToContainerHostname(ip)
    }

    override suspend fun runRuntimeShutdown(service: DockerService, shutdownCleanup: Boolean) {
        val id = service.containerId ?: return

        try {
            try {
                client.stopContainerCmd(id).exec()
                client.waitContainerCmd(id).start().awaitStatusCode()
            } catch (_: NotModifiedException) {
                // container already stopped
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun generateInstance(group: AbstractGroup): DockerService {
        return DockerService(group)
    }

    private fun localUserVolumePath(): String {
        val string = containerId()
        val containerInfo = client.inspectContainerCmd(string).exec()
        val mounts = containerInfo.mounts ?: mutableListOf()

        val targetPath = "/cloud/local"

        val mount = mounts.firstOrNull { it.destination?.path == targetPath }
            ?: throw IllegalStateException("Failed to find mount for path $targetPath")

        return mount.source
            ?: throw IllegalStateException("Failed to get source path for mount $targetPath")
    }

    private fun containerId(): String {
        System.getenv("HOSTNAME")?.let {
            if (it.matches(HOSTNAME_REGEX)) return it
        }

        try {
            val host = Files.readString(Paths.get("/etc/hostname")).trim()
            if (host.matches(HOSTNAME_REGEX)) return host
        } catch (_: Exception) {
        }

        try {
            val lines = Files.readAllLines(Paths.get("/proc/self/mountinfo"))
            lines.forEach { line ->
                HOSTNAME_REGEX.find(line)?.let {
                    return it.value
                }
            }
        } catch (_: Exception) {
        }

        return ""
    }

    companion object {
        private val HOSTNAME_REGEX = Regex("[0-9a-f]{12,64}")
    }
}