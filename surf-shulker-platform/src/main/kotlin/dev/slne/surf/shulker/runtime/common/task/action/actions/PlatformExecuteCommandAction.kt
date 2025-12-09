package dev.slne.surf.shulker.runtime.common.task.action.actions

import dev.slne.surf.shulker.common.os.currentOs
import dev.slne.surf.shulker.runtime.common.PlatformParameters
import dev.slne.surf.shulker.runtime.common.task.PlatformTaskStep
import dev.slne.surf.shulker.runtime.common.task.action.PlatformAction
import java.nio.file.Path

class PlatformExecuteCommandAction(
    val command: String
) : PlatformAction() {
    override suspend fun run(file: Path, step: PlatformTaskStep, environment: PlatformParameters) {
        val builder = ProcessBuilder()

        builder.command(*currentOs.shellPrefix, environment.modifyValueWithEnvironment(command))
        builder.directory(file.toFile())

        val process = builder.start()

        process.waitFor()
    }
}