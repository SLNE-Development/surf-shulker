package dev.slne.surf.shulker.api.module

import kotlinx.serialization.Serializable

@Serializable
data class ShulkerModuleInfo(
    val id: String,
    val moduleName: String,
    val description: String,
    val author: String,
    val main: String,
)