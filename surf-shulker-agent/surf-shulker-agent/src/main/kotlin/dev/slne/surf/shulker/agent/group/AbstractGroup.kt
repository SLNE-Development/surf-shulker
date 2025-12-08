package dev.slne.surf.shulker.agent.group

import dev.slne.surf.shulker.agent.Agent
import dev.slne.surf.shulker.agent.service.AbstractService
import dev.slne.surf.shulker.api.group.Group
import dev.slne.surf.shulker.api.platform.PlatformIndex
import dev.slne.surf.shulker.api.template.Template
import dev.slne.surf.shulker.proto.group.GroupType
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import kotlinx.io.files.Path
import kotlinx.serialization.Contextual
import java.time.OffsetDateTime

open class AbstractGroup(
    name: String,
    minMemory: Int,
    maxMemory: Int,
    minOnlineServices: Int,
    maxOnlineServices: Int,
    platformIndex: PlatformIndex,
    percentageToStartNewService: Double,
    createdAt: @Contextual OffsetDateTime,
    templates: List<Template>,
    properties: Map<String, String>
) : Group(
    name = name,
    _minMemory = minMemory,
    _maxMemory = maxMemory,
    _minOnlineServices = minOnlineServices,
    _maxOnlineServices = maxOnlineServices,
    platformIndex = platformIndex,
    _percentageToStartNewService = percentageToStartNewService,
    createdAt = createdAt,
    templates = templates,
    properties = properties
) {

    val platform get() = PlatformPool.find(platformIndex.name)!!
    val serviceCount: Int get() = services.count()
    val services: ObjectList<AbstractService> get() = Agent.runtime.serviceStorage.findByGroup(this)
    val applicationPlatformFile: Path get() = Path("local/metadata/cache/${platformIndex.name}/${platformIndex.version}/${platformIndex.name}-${platformIndex.version}")
    val playerCount: Int get() = services.sumOf { it.playerCount }

    val isProxy get() = platform.type == GroupType.PROXY
    val isServer get() = platform.type == GroupType.SERVER

    suspend fun update() {
        Agent.runtime.groupStorage.update(this)
    }

    suspend fun shutdownAll() {
        Agent.runtime.serviceStorage.findByGroup(this).forEach { it.shutdown() }
    }

    fun updateMinMemory(value: Int) {
        this.minMemory = value
    }

    fun updateMaxMemory(value: Int) {
        this.maxMemory = value
    }

    fun updateMinOnlineServices(value: Int) {
        this.minOnlineServices = value
    }

    fun updateMaxOnlineServices(value: Int) {
        this.maxOnlineServices = value
    }

    fun updateStartThreshold(value: Double) {
        this.percentageToStartNewService = value
    }

    suspend fun startServices(amount: Int): ObjectList<AbstractService> {
        val startedServices = mutableObjectListOf<AbstractService>()

        repeat(amount) {
            val service = Agent.runtime.factory.generateInstance(this)

            Agent.runtime.serviceStorage.deployAbstractService(service)
            Agent.runtime.factory.bootApplication(service)

            startedServices.add(service)
        }

        return startedServices
    }

    fun canStartServices(amount: Int) =
        maxOnlineServices == -1 || (serviceCount + amount) <= maxOnlineServices

    override fun equals(other: Any?): Boolean {
        return if (other is AbstractGroup) {
            other.name == this.name
        } else {
            false
        }
    }

    override fun hashCode() = javaClass.hashCode()

}