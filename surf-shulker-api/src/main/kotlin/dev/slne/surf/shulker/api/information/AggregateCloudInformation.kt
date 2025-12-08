package dev.slne.surf.shulker.api.information

import dev.slne.surf.shulker.proto.cloudinformation.AggregatedCloudInformationSnapshot
import dev.slne.surf.shulker.proto.cloudinformation.aggregatedCloudInformationSnapshot
import kotlinx.serialization.Contextual
import java.time.OffsetDateTime

data class AggregateCloudInformation(
    val createdAt: @Contextual OffsetDateTime,
    val avgCpu: Double,
    val avgMemory: Double
) {
    fun toSnapshot() = aggregatedCloudInformationSnapshot {
        this.timestamp = createdAt.toString()
        this.avgCpuUsage = avgCpu
        this.avgMemoryUsage = avgMemory
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AggregateCloudInformation

        if (avgCpu != other.avgCpu) return false
        if (avgMemory != other.avgMemory) return false
        if (createdAt != other.createdAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = avgCpu.hashCode()
        result = 31 * result + avgMemory.hashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }

    override fun toString(): String {
        return "AggregateCloudInformation(createdAt=$createdAt, avgCpu=$avgCpu, avgMemory=$avgMemory)"
    }

    companion object {
        fun fromSnapshot(snapshot: AggregatedCloudInformationSnapshot) = AggregateCloudInformation(
            createdAt = OffsetDateTime.parse(snapshot.timestamp),
            avgCpu = snapshot.avgCpuUsage,
            avgMemory = snapshot.avgMemoryUsage
        )
    }
}