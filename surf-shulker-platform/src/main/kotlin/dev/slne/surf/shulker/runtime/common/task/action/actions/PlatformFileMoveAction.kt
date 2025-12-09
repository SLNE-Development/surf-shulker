package dev.slne.surf.shulker.runtime.common.task.action.actions

import dev.slne.surf.shulker.runtime.common.PlatformParameters
import dev.slne.surf.shulker.runtime.common.task.PlatformTaskStep
import dev.slne.surf.shulker.runtime.common.task.action.PlatformAction
import java.nio.file.Path

class PlatformFileMoveAction(
    val oldPath: String,
    val newPath: String
) : PlatformAction() {
    override suspend fun run(file: Path, step: PlatformTaskStep, environment: PlatformParameters) {
        file.resolve(environment.modifyValueWithEnvironment(oldPath)).toFile()
            .renameTo(file.resolve(environment.modifyValueWithEnvironment(newPath)).toFile())
    }
}