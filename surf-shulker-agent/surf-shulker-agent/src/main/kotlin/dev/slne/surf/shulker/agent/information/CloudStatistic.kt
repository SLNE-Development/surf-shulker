package dev.slne.surf.shulker.agent.information

import dev.slne.surf.shulker.agent.Agent
import dev.slne.surf.shulker.api.information.CloudInformation
import dev.slne.surf.shulker.api.utils.os.SystemResources
import dev.slne.surf.shulker.runtime.docker.DockerRuntime
import java.time.OffsetDateTime

data class CloudStatistic(
    val cpuUsage: Double,
    val usedMemory: Double,
    val subscribedEvents: Int,
    val timestamp: OffsetDateTime
) {
    fun toCloudInformation(): CloudInformation {
        val runtime = Agent.runtime
        val runtimeString =
            if (runtime is KubernetesRuntime) "Kubernetes" else if (runtime is DockerRuntime) "Docker" else "Local"

        return CloudInformation(
            runtime.startedAt,
            runtimeString,
            System.getProperty("java.version"),
            cpuUsage,
            usedMemory,
            SystemResources.usedMemory(),
            subscribedEvents,
            timestamp
        )
    }

    companion object {
        fun fromCloudInformation(information: CloudInformation) = CloudStatistic(
            cpuUsage = information.cpuUsage,
            usedMemory = information.memoryUsage,
            subscribedEvents = information.subscribedEvents,
            timestamp = information.timestamp
        )
    }
}