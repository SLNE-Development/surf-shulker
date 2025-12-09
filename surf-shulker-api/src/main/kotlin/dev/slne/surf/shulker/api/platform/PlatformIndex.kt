package dev.slne.surf.shulker.api.platform

import dev.slne.surf.shulker.proto.group.GroupPlatformSnapshot
import dev.slne.surf.shulker.proto.group.groupPlatformSnapshot
import kotlinx.serialization.Serializable

@Serializable
data class PlatformIndex(
    val name: String,
    val version: String
) {
    fun toSnapshot() = groupPlatformSnapshot {
        this.name = this@PlatformIndex.name
        this.version = this@PlatformIndex.version
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlatformIndex

        if (name != other.name) return false
        if (version != other.version) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + version.hashCode()
        return result
    }

    override fun toString(): String {
        return "PlatformIndex(name='$name', version='$version')"
    }

    companion object {
        fun fromSnapshot(snapshot: GroupPlatformSnapshot) = PlatformIndex(
            name = snapshot.name,
            version = snapshot.version
        )
    }
}