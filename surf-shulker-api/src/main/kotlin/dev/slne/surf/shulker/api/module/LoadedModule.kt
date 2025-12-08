package dev.slne.surf.shulker.api.module

data class LoadedModule(
    val module: ShulkerModule,
    val classLoader: ClassLoader
)