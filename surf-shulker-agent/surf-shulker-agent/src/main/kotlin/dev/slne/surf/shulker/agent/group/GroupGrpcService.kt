package dev.slne.surf.shulker.agent.group

import dev.slne.surf.shulker.agent.Agent
import dev.slne.surf.shulker.api.platform.PlatformIndex
import dev.slne.surf.shulker.api.template.Template
import dev.slne.surf.shulker.proto.group.*
import io.grpc.Status
import io.grpc.StatusRuntimeException
import java.time.OffsetDateTime

object GroupGrpcService : GroupControllerGrpcKt.GroupControllerCoroutineImplBase() {
    private val groupStorage get() = Agent.runtime.groupStorage

    override suspend fun find(request: FindGroupRequest): FindGroupResponse {
        val requestName = request.name

        val groupsToReturn = if (request.name.isNotEmpty()) {
            groupStorage.findByName(requestName)?.let { listOf(it) } ?: emptyList()
        } else {
            groupStorage.findAll()
        }

        return findGroupResponse {
            groups.addAll(groupsToReturn.map { it.toSnapshot() })
        }
    }

    override suspend fun create(request: GroupSnapshot): GroupSnapshot {
        if (groupStorage.findByName(request.name) != null) {
            throw StatusRuntimeException(Status.ALREADY_EXISTS)
        }

        val group = request.toAbstract()

        groupStorage.create(group)

        return group.toSnapshot()
    }

    override suspend fun update(request: GroupSnapshot): GroupSnapshot {
        if (groupStorage.findByName(request.name) == null) {
            throw StatusRuntimeException(Status.NOT_FOUND)
        }

        val group = request.toAbstract()

        groupStorage.update(group)

        return group.toSnapshot()
    }

    override suspend fun delete(request: GroupDeleteRequest): GroupSnapshot {
        val group = groupStorage.findByName(request.name)
            ?: throw StatusRuntimeException(Status.NOT_FOUND)

        Agent.runtime.serviceStorage.findByGroup(group).forEach { it.shutdown() }
        groupStorage.delete(group)

        return group.toSnapshot()
    }

    private fun GroupSnapshot.toAbstract() = AbstractGroup(
        name = name,
        minMemory = minimumMemory,
        maxMemory = maximumMemory,
        minOnlineServices = minimumOnline,
        maxOnlineServices = maximumOnline,
        platformIndex = PlatformIndex(platform.name, platform.version),
        percentageToStartNewService = percentageToNewService,
        createdAt = OffsetDateTime.now(),
        templates = Template.fromSnapshotList(templatesList),
        properties = propertiesMap
    )
}