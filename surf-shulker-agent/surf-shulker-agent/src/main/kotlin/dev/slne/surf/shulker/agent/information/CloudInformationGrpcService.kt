package dev.slne.surf.shulker.agent.information

import com.google.protobuf.Empty
import dev.slne.surf.shulker.proto.cloudinformation.*

object CloudInformationGrpcService :
    CloudInformationControllerGrpcKt.CloudInformationControllerCoroutineImplBase() {
    override suspend fun find(request: FindCloudInformationRequest): FindCloudInformationResponse {
        return super.find(request)
    }

    override suspend fun findAll(request: FindAllCloudInformationRequest): FindCloudInformationResponse {
        return super.findAll(request)
    }

    override suspend fun findMinutes(request: AggregatedFindCloudInformationRequest): FindCloudInformationAggregatedResponse {
        return super.findMinutes(request)
    }

    override suspend fun findHours(request: AggregatedFindCloudInformationRequest): FindCloudInformationAggregatedResponse {
        return super.findHours(request)
    }

    override suspend fun findDays(request: AggregatedFindCloudInformationRequest): FindCloudInformationAggregatedResponse {
        return super.findDays(request)
    }

    override suspend fun findAveraged(request: AggregatedFindCloudInformationRequest): FindCloudInformationAggregatedResponse {
        return super.findAveraged(request)
    }

    override suspend fun cleanup(request: CleanupCloudInformationRequest): Empty {
        return super.cleanup(request)
    }
}