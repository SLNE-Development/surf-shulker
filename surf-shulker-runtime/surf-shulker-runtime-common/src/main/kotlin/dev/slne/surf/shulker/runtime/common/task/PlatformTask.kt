package dev.slne.surf.shulker.runtime.common.task

import dev.slne.surf.shulker.runtime.common.PlatformParameters
import java.nio.file.Path

data class PlatformTask(
    val name: String,
    val steps: List<PlatformTaskStep>
) {
    suspend fun runTask(servicePath: Path, environment: PlatformParameters) {
        steps.forEach { it.run(servicePath, environment) }
    }
}