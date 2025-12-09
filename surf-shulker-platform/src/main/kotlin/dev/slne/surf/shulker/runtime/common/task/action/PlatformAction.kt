package dev.slne.surf.shulker.runtime.common.task.action

import dev.slne.surf.shulker.runtime.common.PlatformParameters
import dev.slne.surf.shulker.runtime.common.task.PlatformTaskStep
import java.nio.file.Path

abstract class PlatformAction {
    abstract suspend fun run(
        file: Path,
        step: PlatformTaskStep,
        environment: PlatformParameters
    )
}