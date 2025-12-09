package dev.slne.surf.shulker.runtime.common.task.action.actions

import dev.slne.surf.shulker.runtime.common.PlatformParameters
import dev.slne.surf.shulker.runtime.common.task.PlatformTaskStep
import dev.slne.surf.shulker.runtime.common.task.action.PlatformAction
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createParentDirectories

class PlatformFileWriteAction(
    val content: String
) : PlatformAction() {
    override suspend fun run(file: Path, step: PlatformTaskStep, environment: PlatformParameters) {
        file.createParentDirectories()
        
        Files.writeString(file, environment.modifyValueWithEnvironment(content))
    }
}