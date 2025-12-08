package dev.slne.surf.shulker.api.information

import it.unimi.dsi.fastutil.objects.ObjectList
import java.time.OffsetDateTime
import kotlin.time.Duration

interface SharedCloudInformationProvider<C : CloudInformation> {
    suspend fun find(): C

    suspend fun find(from: OffsetDateTime, to: OffsetDateTime): ObjectList<C>

    suspend fun findAll(): ObjectList<C>

    suspend fun findMinutes(
        from: OffsetDateTime,
        to: OffsetDateTime
    ): ObjectList<AggregateCloudInformation>

    suspend fun findHours(
        from: OffsetDateTime,
        to: OffsetDateTime
    ): ObjectList<AggregateCloudInformation>

    suspend fun findDays(
        from: OffsetDateTime,
        to: OffsetDateTime
    ): ObjectList<AggregateCloudInformation>

    suspend fun findAverage(
        from: OffsetDateTime,
        to: OffsetDateTime
    ): AggregateCloudInformation

    suspend fun cleanup(maxAge: Duration)
}