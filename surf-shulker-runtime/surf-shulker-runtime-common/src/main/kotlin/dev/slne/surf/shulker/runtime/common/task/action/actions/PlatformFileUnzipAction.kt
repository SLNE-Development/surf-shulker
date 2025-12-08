package dev.slne.surf.shulker.runtime.common.task.action.actions

import dev.slne.surf.shulker.runtime.common.PlatformParameters
import dev.slne.surf.shulker.runtime.common.task.PlatformTaskStep
import dev.slne.surf.shulker.runtime.common.task.action.PlatformAction
import java.nio.file.Path
import java.util.zip.ZipFile
import kotlin.io.path.createDirectory
import kotlin.io.path.createParentDirectories
import kotlin.io.path.notExists
import kotlin.io.path.outputStream

class PlatformFileUnzipAction : PlatformAction() {
    override suspend fun run(file: Path, step: PlatformTaskStep, environment: PlatformParameters) {
        if (file.notExists()) return

        ZipFile(file.toFile()).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                val newFile = file.parent.resolve(entry.name)

                try {
                    newFile.createParentDirectories()
                } catch (e: Exception) {
                    println(e.message)
                }

                if (entry.isDirectory) {
                    if (newFile.notExists()) {
                        newFile.createDirectory()
                    }
                } else {
                    zip.getInputStream(entry).use { inputStream ->
                        newFile.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
            }
        }
    }
}