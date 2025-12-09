package dev.slne.surf.shulker.core.group

import dev.slne.surf.shulker.api.ShulkerApi
import dev.slne.surf.shulker.api.group.Group
import dev.slne.surf.shulker.api.platform.PlatformIndex
import dev.slne.surf.shulker.api.property.PropertyHolder
import dev.slne.surf.shulker.api.template.Template
import dev.slne.surf.shulker.core.service.AbstractService
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
    properties: PropertyHolder
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
    val applicationPlatformFile: Path get() = Path("local/metadata/cache/${platformIndex.name}/${platformIndex.version}/${platformIndex.name}-${platformIndex.version}")

    suspend fun isProxy() = platform()?.type == GroupType.PROXY
    suspend fun isServer() = platform()?.type == GroupType.SERVER

    suspend fun platform() = ShulkerApi.platformProvider.findByName(platformIndex.name)
    suspend fun services() = ShulkerApi.serviceProvider.findByGroup(this)
    suspend fun serviceCount() = services().count()
    suspend fun playerCount() = services().sumOf { it.playerCount }

    suspend fun update() {
        ShulkerApi.groupProvider.update(this)
    }

    suspend fun shutdownAll() {
        ShulkerApi.serviceProvider.findByGroup(this).forEach { it.shutdown() }
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
            val service = ShulkerApi.factory.generateInstance(this)

            Agent.runtime.serviceStorage.deployAbstractService(service)
            Agent.runtime.factory.bootApplication(service)

            startedServices.add(service)
        }

        return startedServices
    }

    suspend fun canStartServices(amount: Int) =
        maxOnlineServices == -1 || (serviceCount() + amount) <= maxOnlineServices

    override fun equals(other: Any?): Boolean {
        return if (other is AbstractGroup) {
            other.name == this.name
        } else {
            false
        }
    }

    override fun hashCode() = javaClass.hashCode()

}