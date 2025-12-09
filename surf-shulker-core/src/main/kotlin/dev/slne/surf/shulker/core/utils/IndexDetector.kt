package dev.slne.surf.shulker.core.utils

import dev.slne.surf.shulker.api.ShulkerApi
import dev.slne.surf.shulker.core.group.AbstractGroup
import kotlinx.coroutines.runBlocking

object IndexDetector {
    // TODO: Fix blocking
    fun findIndex(group: AbstractGroup): Int = runBlocking {
        var id = 1

        while (ShulkerApi.serviceProvider.findAll().stream()
                .anyMatch { it.groupName == group.name && it.id == id }
        ) {
            id++
        }

        return@runBlocking id
    }
}