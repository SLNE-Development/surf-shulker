package dev.slne.surf.shulker.runtime.common.task

import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf

object PlatformTaskPool {
    private val tasks = mutableObjectListOf<PlatformTask>()
    val size get() = tasks.size

    fun attach(task: PlatformTask) {
        tasks.add(task)
    }

    fun findByName(name: String) = tasks.firstOrNull { it.name == name }
}