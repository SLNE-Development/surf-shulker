package dev.slne.surf.shulker.runtime.common.metadata

import dev.slne.surf.shulker.runtime.common.PLATFORM_GSON
import dev.slne.surf.shulker.runtime.common.PLATFORM_PATH
import dev.slne.surf.shulker.runtime.common.Platform
import dev.slne.surf.shulker.runtime.common.PlatformPool
import dev.slne.surf.shulker.runtime.common.exception.DuplicatePlatformActionException
import dev.slne.surf.shulker.runtime.common.task.PlatformTask
import dev.slne.surf.shulker.runtime.common.task.PlatformTaskPool
import dev.slne.surf.surfapi.core.api.util.logger
import java.nio.file.Files
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.system.exitProcess

object MetadataReader {
    private val log = logger()

    fun combineData() {
        if (!this.readTasksMetadata() || !this.readPlatformsMetadata()) {
            log.atSevere().log("Could not load platform metadata!")
            exitProcess(-1)
        }
    }

    private fun readTasksMetadata(): Boolean {
        val path = PLATFORM_PATH.resolve("tasks")

        if (!path.exists()) {
            return false
        }

        path.listDirectoryEntries().forEach { file ->
            val task = PLATFORM_GSON.fromJson(Files.readString(file), PlatformTask::class.java)

            if (PlatformTaskPool.findByName(task.name) != null) {
                throw DuplicatePlatformActionException(task.name)
            }

            PlatformTaskPool.attach(task)
        }

        return true
    }

    private fun readPlatformsMetadata(): Boolean {
        val path = PLATFORM_PATH.resolve("platforms")

        if (!path.exists()) {
            return false
        }

        path.listDirectoryEntries().forEach { file ->
            val platform = PLATFORM_GSON.fromJson(Files.readString(file), Platform::class.java)

            PlatformPool.attach(platform)
        }

        return true
    }
}