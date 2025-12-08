package dev.slne.surf.shulker.runtime.common.bridge

import kotlinx.serialization.Serializable

@Serializable
data class BridgeConfig(
    val className: String,
    val type: BridgeType
)