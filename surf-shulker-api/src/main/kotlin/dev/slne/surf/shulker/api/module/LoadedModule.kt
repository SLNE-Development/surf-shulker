package dev.slne.surf.shulker.api.module

import java.net.URLClassLoader

data class LoadedModule(
    val module: ShulkerModule,
    val classLoader: URLClassLoader,
    val metadata: ShulkerModuleInfo
)