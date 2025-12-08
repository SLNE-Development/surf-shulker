package dev.slne.surf.shulker.runtime.common.bridge

import kotlinx.serialization.Serializable

@Serializable
enum class BridgeType {
    ON_PREMISE,
    OFF_PREMISE
}