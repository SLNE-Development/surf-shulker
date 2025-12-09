package dev.slne.surf.shulker.runtime.docker

import dev.slne.surf.shulker.agent.Agent
import dev.slne.surf.shulker.agent.utils.AbstractJob
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object DockerCloudInformationJob : AbstractJob("shulker-local-cloud-information") {
    private var lastCleanUp: Long = 0L

    override suspend fun run() {
        while (true) {
            val now = System.currentTimeMillis()
            Agent.informationStorage.saveCurrentCloudInformation()

            if (now - lastCleanUp >= 5.minutes.inWholeMilliseconds) {
                Agent.informationStorage.cleanup(7.days)
                lastCleanUp = now
            }

            delay(5.seconds)
        }
    }
}