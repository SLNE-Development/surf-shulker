package dev.slne.surf.shulker.agent.information

import dev.slne.surf.shulker.agent.Agent
import dev.slne.surf.shulker.api.information.AggregateCloudInformation
import dev.slne.surf.shulker.api.information.CloudInformation
import dev.slne.surf.shulker.api.information.StatAggregate
import dev.slne.surf.shulker.api.utils.os.SystemResources
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.toObjectList
import it.unimi.dsi.fastutil.objects.ObjectList
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.time.Duration

object CloudInformationStorageImpl : CloudInformationStorage {
    private val cachedInformation = mutableObjectListOf<CloudStatistic>()
    private val minuteAggregates = mutableObject2ObjectMapOf<Long, StatAggregate>()
    private val hourAggregates = mutableObject2ObjectMapOf<Long, StatAggregate>()
    private val dayAggregates = mutableObject2ObjectMapOf<Long, StatAggregate>()

    override fun addCloudInformation(cloudInformation: CloudInformation) {
        cachedInformation.add(CloudStatistic.fromCloudInformation(cloudInformation))
    }

    override fun removeCloudInformation(cloudInformation: CloudInformation) {
        cachedInformation.remove(CloudStatistic.fromCloudInformation(cloudInformation))
    }

    private fun updateAggregate(
        map: MutableMap<Long, StatAggregate>,
        bucket: Long,
        cpu: Double,
        memory: Double
    ) {
        val aggregate = map.getOrPut(bucket) { StatAggregate() }
        aggregate.cpuSum += cpu
        aggregate.memorySum += memory
        aggregate.count += 1
    }

    override fun saveCurrentCloudInformation() {
        val now = OffsetDateTime.now()
        val epochMillis = now.toEpochMillis()

        val cpu = SystemResources.cpuUsage()
        val memory = SystemResources.usedMemory()

        cachedInformation.add(
            CloudStatistic(
                cpu,
                memory,
                Agent.eventProvider.registeredAmount,
                now
            )
        )

        updateAggregate(minuteAggregates, epochMillis / 60000, cpu, memory)
        updateAggregate(hourAggregates, epochMillis / 3600000, cpu, memory)
        updateAggregate(dayAggregates, epochMillis / 86400000, cpu, memory)
    }

    override suspend fun find(): CloudInformation {
        return cachedInformation.last().toCloudInformation()
    }

    override suspend fun find(
        from: OffsetDateTime,
        to: OffsetDateTime
    ): ObjectList<CloudInformation> {
        return cachedInformation.filter { ci ->
            ci.timestamp in from..to
        }.map { it.toCloudInformation() }.toObjectList()
    }

    override suspend fun findAll(): ObjectList<CloudInformation> {
        return cachedInformation.map { it.toCloudInformation() }.toObjectList()
    }

    private fun OffsetDateTime.toEpochMillis() = toInstant().toEpochMilli()
    private fun Long.toOffsetDateTime(offset: ZoneOffset = ZoneOffset.UTC) =
        OffsetDateTime.ofInstant(Instant.ofEpochMilli(this), offset)

    private fun findAggregate(
        from: OffsetDateTime,
        to: OffsetDateTime,
        slotMillis: Long,
        map: Map<Long, StatAggregate>
    ): ObjectList<AggregateCloudInformation> {
        val fromBucket = from.toEpochMillis() / slotMillis
        val toBucket = to.toEpochMillis() / slotMillis

        return map
            .filterKeys { it in fromBucket..toBucket }
            .map { (bucket, agg) ->
                val timestampMillis = bucket * slotMillis

                AggregateCloudInformation(
                    createdAt = timestampMillis.toOffsetDateTime(from.offset),
                    avgCpu = if (agg.count > 0) agg.cpuSum / agg.count else 0.0,
                    avgMemory = if (agg.count > 0) agg.memorySum / agg.count else 0.0,
                )
            }.toObjectList()
    }

    override suspend fun findMinutes(
        from: OffsetDateTime,
        to: OffsetDateTime
    ): ObjectList<AggregateCloudInformation> {
        return findAggregate(from, to, 60000, minuteAggregates)
    }

    override suspend fun findHours(
        from: OffsetDateTime,
        to: OffsetDateTime
    ): ObjectList<AggregateCloudInformation> {
        return findAggregate(from, to, 3600000, hourAggregates)
    }

    override suspend fun findDays(
        from: OffsetDateTime,
        to: OffsetDateTime
    ): ObjectList<AggregateCloudInformation> {
        return findAggregate(from, to, 86400000, dayAggregates)
    }

    override suspend fun findAverage(
        from: OffsetDateTime,
        to: OffsetDateTime
    ): AggregateCloudInformation {
        val fromMillis = from.toEpochMillis()
        val toMillis = to.toEpochMillis()

        val relevant = cachedInformation.filter { ci ->
            val tsMillis = ci.timestamp.toEpochMillis()
            tsMillis in fromMillis..toMillis
        }

        val agg = StatAggregate()

        relevant.forEach {
            agg.cpuSum += it.cpuUsage
            agg.memorySum += it.usedMemory
            agg.count += 1
        }

        return AggregateCloudInformation(
            createdAt = from,
            avgCpu = if (agg.count > 0) agg.cpuSum / agg.count else 0.0,
            avgMemory = if (agg.count > 0) agg.memorySum / agg.count else 0.0,
        )
    }

    override suspend fun cleanup(maxAge: Duration) {
        val cutoffMillis =
            OffsetDateTime.now().minusNanos(maxAge.inWholeNanoseconds).toEpochMillis()

        cachedInformation.removeIf { it.timestamp.toEpochMillis() < cutoffMillis }

        val minuteCutoff = cutoffMillis / 60000
        minuteAggregates.keys.removeIf { it < minuteCutoff }

        val hourCutoff = cutoffMillis / 3600000
        hourAggregates.keys.removeIf { it < hourCutoff }

        val dayCutoff = cutoffMillis / 86400000
        dayAggregates.keys.removeIf { it < dayCutoff }
    }
}