package dev.slne.surf.shulker.api.information

import dev.slne.surf.shulker.proto.cloudinformation.CloudInformationSnapshot
import dev.slne.surf.shulker.proto.cloudinformation.cloudInformationSnapshot
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
open class CloudInformation(
    val started: @Contextual OffsetDateTime,
    val runtime: String,
    val javaVersion: String,
    val cpuUsage: Double,
    val memoryUsage: Double,
    val maxMemory: Double,
    val subscribedEvents: Int,
    val timestamp: @Contextual OffsetDateTime
) {
    fun toSnapshot() = cloudInformationSnapshot {
        this.startedAt = this@CloudInformation.started.toString()
        this.runtime = this@CloudInformation.runtime
        this.javaVersion = this@CloudInformation.javaVersion
        this.cpuUsage = this@CloudInformation.cpuUsage
        this.memoryUsage = this@CloudInformation.memoryUsage
        this.maxMemory = this@CloudInformation.maxMemory
        this.subscribedEvents = this@CloudInformation.subscribedEvents
        this.timestamp = this@CloudInformation.timestamp.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CloudInformation

        if (cpuUsage != other.cpuUsage) return false
        if (memoryUsage != other.memoryUsage) return false
        if (maxMemory != other.maxMemory) return false
        if (subscribedEvents != other.subscribedEvents) return false
        if (started != other.started) return false
        if (runtime != other.runtime) return false
        if (javaVersion != other.javaVersion) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cpuUsage.hashCode()
        result = 31 * result + memoryUsage.hashCode()
        result = 31 * result + maxMemory.hashCode()
        result = 31 * result + subscribedEvents
        result = 31 * result + started.hashCode()
        result = 31 * result + runtime.hashCode()
        result = 31 * result + javaVersion.hashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }

    override fun toString(): String {
        return "CloudInformation(started=$started, runtime='$runtime', javaVersion='$javaVersion', cpuUsage=$cpuUsage, memoryUsage=$memoryUsage, maxMemory=$maxMemory, subscribedEvents=$subscribedEvents, timestamp=$timestamp)"
    }

    companion object {
        fun fromSnapshot(snapshot: CloudInformationSnapshot) = CloudInformation(
            started = OffsetDateTime.parse(snapshot.startedAt),
            runtime = snapshot.runtime,
            javaVersion = snapshot.javaVersion,
            cpuUsage = snapshot.cpuUsage,
            memoryUsage = snapshot.memoryUsage,
            maxMemory = snapshot.maxMemory,
            subscribedEvents = snapshot.subscribedEvents,
            timestamp = OffsetDateTime.parse(snapshot.timestamp)
        )
    }
}