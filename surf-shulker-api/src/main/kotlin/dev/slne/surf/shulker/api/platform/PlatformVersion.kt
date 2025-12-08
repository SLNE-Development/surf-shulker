package dev.slne.surf.shulker.api.platform

import dev.slne.surf.shulker.proto.platform.PlatformVersionSnapshot
import dev.slne.surf.shulker.proto.platform.platformVersionSnapshot
import kotlinx.serialization.Serializable

@Serializable
data class PlatformVersion(
    val version: String
) {
    fun toSnapshot() = platformVersionSnapshot {
        this.version = this@PlatformVersion.version
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlatformVersion

        return version == other.version
    }

    override fun hashCode(): Int {
        return version.hashCode()
    }

    override fun toString(): String {
        return "PlatformVersion(version='$version')"
    }

    companion object {
        fun bindSnapshot(snapshot: PlatformVersionSnapshot) = PlatformVersion(
            version = snapshot.version
        )
    }
}