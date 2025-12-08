package dev.slne.surf.shulker.api.module

import dev.slne.surf.shulker.spring.utils.findAnnotation

interface ShulkerModule {
    val id: String
        get() = info.id

    val info: ShulkerModuleInfo
        get() = this::class.findAnnotation<ShulkerModuleInfo>()
            ?: error("ShulkerModuleInfo annotation is missing on ${this::class.qualifiedName}")

    suspend fun onLoad()
    suspend fun onEnable()
    suspend fun onDisable()
}