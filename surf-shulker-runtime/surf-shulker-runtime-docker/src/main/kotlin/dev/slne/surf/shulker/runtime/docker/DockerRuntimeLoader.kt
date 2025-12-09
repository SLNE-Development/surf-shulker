package dev.slne.surf.shulker.runtime.docker

import dev.slne.surf.shulker.agent.runtime.RuntimeLoader
import java.nio.file.Files
import java.nio.file.Paths

object DockerRuntimeLoader : RuntimeLoader {
    override fun runnable(): Boolean {
        return try {
            return Files.exists(Paths.get("/.dockerenv")) || Files.exists(Paths.get("/.containerenv"))
        } catch (e: Exception) {
            false
        }
    }

    override fun instance(): Runtime {
        return DockerRuntime()
    }
}