package dev.slne.surf.shulker.api.player

import dev.slne.surf.shulker.proto.player.PlayerSnapshot
import dev.slne.surf.shulker.proto.player.playerSnapshot
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
open class ShulkerPlayer(
    val uuid: @Contextual UUID,
    val name: String,
    val currentServiceName: String,
) {
    val uniqueId get() = uuid.toString()

    fun toSnapshot() = playerSnapshot {
        uniqueId = uuid.toString()
        this.name = name
        this.currentServiceName = currentServiceName
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShulkerPlayer

        if (uuid != other.uuid) return false
        if (name != other.name) return false
        if (currentServiceName != other.currentServiceName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + currentServiceName.hashCode()
        return result
    }

    override fun toString(): String {
        return "ShulkerPlayer(uuid=$uuid, name='$name', currentServiceName='$currentServiceName', uniqueId='$uniqueId')"
    }

    companion object {
        fun bindSnapshot(snapshot: PlayerSnapshot) = ShulkerPlayer(
            uuid = UUID.fromString(snapshot.uniqueId),
            name = snapshot.name,
            currentServiceName = snapshot.currentServiceName,
        )
    }
}