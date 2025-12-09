package dev.slne.surf.shulker.runtime.docker

import com.github.dockerjava.api.DockerClient
import dev.slne.surf.shulker.agent.Agent
import dev.slne.surf.shulker.agent.runtime.RuntimeServiceStorage
import dev.slne.surf.shulker.agent.service.AbstractService
import dev.slne.surf.shulker.api.group.Group
import dev.slne.surf.shulker.api.service.Service
import dev.slne.surf.shulker.api.service.SharedBootConfig
import dev.slne.surf.shulker.proto.group.GroupType
import dev.slne.surf.shulker.proto.service.ServiceSnapshot
import dev.slne.surf.surfapi.core.api.util.toObjectList
import it.unimi.dsi.fastutil.objects.ObjectList
import java.util.concurrent.CopyOnWriteArrayList

class DockerRuntimeServiceStorage(
    val client: DockerClient
) : RuntimeServiceStorage<DockerService> {
    private val services = CopyOnWriteArrayList<DockerService>()

    override suspend fun findAll() = services.toObjectList()

    override suspend fun findByName(name: String): DockerService? {
        return services.firstOrNull { it.name.equals(name, true) }
    }

    override suspend fun findByType(type: GroupType): ObjectList<DockerService> {
        return services.filter { it.type == type }.toObjectList()
    }

    override suspend fun findByGroupName(groupName: String): ObjectList<DockerService> {
        return services.filter { it.groupName.equals(groupName, true) }.toObjectList()
    }

    override suspend fun findByGroup(group: Group): ObjectList<DockerService> {
        return findByGroupName(group.name)
    }

    override suspend fun bootInstanceWithConfiguration(
        name: String,
        configuration: SharedBootConfig
    ): ServiceSnapshot {
        TODO("Not yet implemented")
    }

    override suspend fun shutdownService(service: Service): ServiceSnapshot {
        return Agent.runtime.factory.shutdownApplication(
            findByName(service.name)
                ?: throw IllegalStateException("Service ${service.name} not found in DockerRuntimeServiceStorage")
        )
    }

    override suspend fun deployService(service: DockerService) {
        services.add(service)
    }

    override suspend fun dropService(service: DockerService) {
        services.remove(service)
    }

    override fun implementedService(abstractService: AbstractService): DockerService {
        return abstractService as? DockerService
            ?: throw IllegalStateException("Service ${abstractService.name} not implemented: ${abstractService.javaClass}")
    }
}