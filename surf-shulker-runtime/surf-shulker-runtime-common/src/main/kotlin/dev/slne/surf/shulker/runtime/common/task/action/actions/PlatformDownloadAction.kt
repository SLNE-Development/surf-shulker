package dev.slne.surf.shulker.runtime.common.task.action.actions

import dev.slne.surf.shulker.runtime.common.PlatformParameters
import dev.slne.surf.shulker.runtime.common.task.PlatformTaskStep
import dev.slne.surf.shulker.runtime.common.task.action.PlatformAction
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.outputStream

class PlatformDownloadAction(
    val url: String
) : PlatformAction() {
    override suspend fun run(file: Path, step: PlatformTaskStep, environment: PlatformParameters) {
        val processedUrl = environment.modifyValueWithEnvironment(url)

        file.createParentDirectories()

        URI(processedUrl).toURL().openStream().buffered().use { input ->
            file.outputStream().use { outputStream ->
                input.copyTo(outputStream)
            }
        }
    }
}