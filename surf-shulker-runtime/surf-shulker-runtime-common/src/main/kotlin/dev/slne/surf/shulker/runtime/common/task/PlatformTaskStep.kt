package dev.slne.surf.shulker.runtime.common.task

import dev.slne.surf.shulker.runtime.common.PlatformParameters
import dev.slne.surf.shulker.runtime.common.task.action.PlatformAction
import java.nio.file.Path

class PlatformTaskStep(
    val name: String,
    val description: String,
    val fileName: String,
    val action: PlatformAction
) {
    suspend fun run(servicePath: Path, environment: PlatformParameters) {
        action.run(
            servicePath.resolve(environment.modifyValueWithEnvironment(fileName)),
            this,
            environment
        )
    }
}