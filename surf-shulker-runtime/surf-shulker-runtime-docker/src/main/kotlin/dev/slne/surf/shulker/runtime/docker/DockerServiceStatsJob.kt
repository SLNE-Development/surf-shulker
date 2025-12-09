package dev.slne.surf.shulker.runtime.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.model.Statistics
import dev.slne.surf.shulker.agent.runtime.abstract.AbstractServiceStatsJob
import dev.slne.surf.shulker.api.utils.math.convertBytesToMegabytes
import java.math.BigDecimal
import java.math.RoundingMode

class DockerServiceStatsJob(
    private val client: DockerClient
) : AbstractServiceStatsJob<DockerService>() {
    override suspend fun detectService(service: DockerService) {
        val containerId = service.containerId ?: return
        val stats = getStats(containerId) ?: return

        val cpuPercentage = calculateCpuUsage(stats)
        if (cpuPercentage != null) {
            service.updateCpuUsage(cpuPercentage)
        }

        val usedMemory = stats.memoryStats?.usage
        if (usedMemory != null) {
            service.updateMemoryUsage(convertBytesToMegabytes(usedMemory))
        }
    }

    private fun calculateCpuUsage(stats: Statistics): Double? {
        val cpuUsage = stats.cpuStats?.cpuUsage?.totalUsage ?: return null
        val preCpuUsage = stats.preCpuStats?.cpuUsage?.totalUsage ?: return null
        val systemCpuUsage = stats.cpuStats?.systemCpuUsage ?: return null
        val preSystemCpuUsage = stats.preCpuStats?.systemCpuUsage ?: return null
        val onlineCpus = stats.cpuStats?.onlineCpus ?: return null

        val cpuDelta = cpuUsage - preCpuUsage
        val systemDelta = systemCpuUsage - preSystemCpuUsage

        val cpuPercentage = if (systemDelta > 0 && cpuDelta > 0) {
            (cpuDelta.toDouble() / systemDelta.toDouble()) * onlineCpus.toDouble() * 100.0
        } else {
            0.0
        }

        return BigDecimal(cpuPercentage)
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
    }

    private fun getStats(containerId: String): Statistics? {
        var result: Statistics? = null

        val callback = object : ResultCallback.Adapter<Statistics>() {
            override fun onNext(stats: Statistics?) {
                result = stats
                close()
            }
        }

        client.statsCmd(containerId)
            .withNoStream(true)
            .exec(callback)
            .awaitCompletion()

        return result
    }
}