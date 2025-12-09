@file:Suppress("UNCHECKED_CAST")

package dev.slne.surf.shulker.runtime.common.abstract

import dev.slne.surf.shulker.agent.Agent
import dev.slne.surf.shulker.agent.utils.AbstractJob
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

abstract class AbstractServiceStatsJob<T> : AbstractJob("shulker-service-stats-detector-job") {
    override suspend fun run() {
        while (true) {
            Agent.runtime.serviceStorage.findAll().forEach {
                detectService(it as T)
            }

            delay(1.seconds)
        }
    }

    abstract suspend fun detectService(service: T)
}