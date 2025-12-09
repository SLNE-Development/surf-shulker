package dev.slne.surf.shulker.runtime.common.abstract

import dev.slne.surf.shulker.agent.Agent
import dev.slne.surf.shulker.agent.group.AbstractGroup
import dev.slne.surf.shulker.agent.inShutdownProcess
import dev.slne.surf.shulker.agent.utils.AbstractJob
import dev.slne.surf.shulker.api.property.PRIORITY
import dev.slne.surf.shulker.proto.service.ServiceState
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

open class AbstractThreadedRuntimeQueue : AbstractJob("shulker-local-runtime-queue") {
    override suspend fun run() {
        val runtime = Agent.runtime

        while (!inShutdownProcess) {
            runtime.groupStorage.findAll()
                .sortedByDescending { it.properties[PRIORITY] ?: -1 }
                .forEach { group ->
                    val required = requiredServersThatStart(group)

                    repeat(required) {
                        if (group.services.size >= group.maxOnlineServices && group.maxOnlineServices != -1) {
                            return@repeat
                        }

                        val service = runtime.factory.generateInstance(group)

                        runtime.serviceStorage.deployAbstractService(service)
                        runtime.factory.bootApplication(service)
                    }
                }

            delay(1.seconds)
        }
    }

    private fun requiredServersThatStart(group: AbstractGroup): Int {
        var required = (group.minOnlineServices - group.serviceCount).coerceAtLeast(0)
        val avgMaxPlayers = group.services.asSequence()
            .filter { it.maxPlayerCount != 1 }
            .map { it.maxPlayerCount }
            .average()

        val avgOnlinePlayers = group.services.asSequence()
            .filter { it.maxPlayerCount != 1 }
            .map { it.playerCount }
            .average()

        val averagesAreValid = avgMaxPlayers > 0 && avgOnlinePlayers > 0

        if (required <= 0 && averagesAreValid && group.services.all { it.state == ServiceState.ONLINE }) {
            val currentUsagePercent = (avgOnlinePlayers / avgMaxPlayers) * 100.0

            if (currentUsagePercent >= group.percentageToStartNewService) {
                required += 1
            }
        }

        return required
    }
}