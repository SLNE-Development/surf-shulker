package dev.slne.surf.shulker.api.platform

import dev.slne.surf.shulker.proto.group.GroupType
import dev.slne.surf.shulker.proto.platform.PlatformSnapshot
import dev.slne.surf.shulker.proto.platform.platformSnapshot
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import kotlinx.serialization.Serializable

@Serializable
open class Platform(
    val name: String,
    val type: GroupType,
    val versions: List<PlatformVersion>
) {
    fun toSnapshot() = platformSnapshot {
        this.name = this@Platform.name
        this.type = this@Platform.type
        this.versions.addAll(this@Platform.versions.map { it.toSnapshot() })
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Platform

        if (name != other.name) return false
        if (type != other.type) return false
        if (versions != other.versions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + versions.hashCode()
        return result
    }

    override fun toString(): String {
        return "Platform(name='$name', type=$type, versions=$versions)"
    }
    
    companion object {
        fun bindSnapshot(snapshot: PlatformSnapshot) = Platform(
            name = snapshot.name,
            type = snapshot.type,
            snapshot.versionsList.mapTo(mutableObjectListOf()) { PlatformVersion.bindSnapshot(it) }
        )
    }
}