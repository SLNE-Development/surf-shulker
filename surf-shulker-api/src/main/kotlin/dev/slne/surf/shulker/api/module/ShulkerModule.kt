package dev.slne.surf.shulker.api.module

interface ShulkerModule {
    suspend fun onLoad()
    suspend fun onEnable()
    suspend fun onDisable()
}