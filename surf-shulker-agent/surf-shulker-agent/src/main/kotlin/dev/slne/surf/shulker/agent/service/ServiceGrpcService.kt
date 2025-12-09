package dev.slne.surf.shulker.agent.service

import dev.slne.surf.shulker.agent.Agent
import dev.slne.surf.shulker.api.template.Template
import dev.slne.surf.shulker.core.service.AbstractService
import dev.slne.surf.shulker.proto.service.*
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.toMutableObjectList
import io.grpc.Status
import io.grpc.StatusRuntimeException

object ServiceGrpcService : ServiceControllerGrpcKt.ServiceControllerCoroutineImplBase() {
    private val runtimeFactory get() = Agent.runtime.factory
    private val serviceStorage get() = Agent.runtime.serviceStorage
    private val groupStorage get() = Agent.runtime.groupStorage

    override suspend fun find(request: ServiceFindRequest): ServiceFindResponse {
        val abstractServices = mutableObjectListOf<AbstractService>()

        if (request.hasName()) {
            val service = serviceStorage.findByName(request.name)

            if (service != null) {
                abstractServices.add(service)
            }
        } else if (request.hasGroupName()) {
            abstractServices.addAll(serviceStorage.findByGroupName(request.groupName))
        } else {
            serviceStorage.findAll().forEach {
                if (!request.hasType() || it.type == request.type) {
                    abstractServices.add(it)
                }
            }
        }

        return serviceFindResponse {
            this.services.addAll(abstractServices.map { it.toSnapshot() })
        }
    }

    override suspend fun boot(request: ServiceBootRequest): ServiceSnapshot {
        val group = groupStorage.findByName(request.groupName)
            ?: throw StatusRuntimeException(Status.NOT_FOUND)

        val service = runtimeFactory.generateInstance(group)

        serviceStorage.deployAbstractService(service)
        runtimeFactory.bootApplication(service)

        return service.toSnapshot()
    }

    override suspend fun bootWithConfiguration(request: ServiceBootWithConfigurationRequest): ServiceSnapshot {
        val group = groupStorage.findByName(request.groupName)
            ?: throw StatusRuntimeException(Status.NOT_FOUND)

        val service = runtimeFactory.generateInstance(group)

        if (request.hasMinimumMemory()) {
            service.updateMinMemory(request.minimumMemory)
        }

        if (request.hasMaximumMemory()) {
            service.updateMaxMemory(request.maximumMemory)
        }

        val updatedTemplates = service.templates.toMutableObjectList()
        updatedTemplates += Template.fromSnapshotList(request.templatesList)

        request.excludedTemplatesList.forEach { template ->
            updatedTemplates.removeIf { it.name == template.name }
        }

        service.templates = updatedTemplates
        service.properties += request.propertiesMap

        serviceStorage.deployAbstractService(service)
        runtimeFactory.bootApplication(service)

        return service.toSnapshot()
    }

    override suspend fun shutdown(request: ServiceShutdownRequest): ServiceShutdownResponse {
        val service = serviceStorage.findByName(request.name)
            ?: throw StatusRuntimeException(Status.NOT_FOUND)

        runtimeFactory.shutdownApplication(service)

        return serviceShutdownResponse {
            this.service = service.toSnapshot()
        }
    }
}