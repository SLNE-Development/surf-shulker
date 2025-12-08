package dev.slne.surf.shulker.api.service

import dev.slne.surf.shulker.proto.service.ServiceInformationSnapshot
import dev.slne.surf.shulker.proto.service.serviceInformationSnapshot
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class ServiceInformation(
    val createdAt: @Contextual OffsetDateTime
) {
    fun toSnapshot() = serviceInformationSnapshot {
        this.createdAt = this@ServiceInformation.createdAt.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServiceInformation

        return createdAt == other.createdAt
    }

    override fun hashCode(): Int {
        return createdAt.hashCode()
    }

    override fun toString(): String {
        return "ServiceInformation(createdAt=$createdAt)"
    }

    companion object {
        fun fromSnapshot(snapshot: ServiceInformationSnapshot) = ServiceInformation(
            createdAt = OffsetDateTime.parse(snapshot.createdAt)
        )
    }
}