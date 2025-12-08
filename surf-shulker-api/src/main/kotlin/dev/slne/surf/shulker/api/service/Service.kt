package dev.slne.surf.shulker.api.service

import dev.slne.surf.shulker.api.ShulkerApi
import dev.slne.surf.shulker.api.template.Template
import dev.slne.surf.shulker.proto.group.GroupType
import dev.slne.surf.shulker.proto.service.ServiceSnapshot
import dev.slne.surf.shulker.proto.service.ServiceState
import dev.slne.surf.shulker.proto.service.serviceSnapshot
import kotlinx.serialization.Serializable

@Serializable
open class Service(
    val groupName: String,
    val id: Int,
    var state: ServiceState,
    val type: GroupType,
    var properties: Map<String, String>,
    private var _hostname: String,
    val port: Int,
    var templates: List<Template>,
    val information: ServiceInformation,
    private var _minMemory: Int,
    private var _maxMemory: Int,
    private var _playerCount: Int = -1,
    private var _maxPlayerCount: Int = -1,
    private var _memoryUsage: Double = -1.0,
    private var _cpuUsage: Double = -1.0,
    private var _motd: String = "",
) {
    var minMemory = _minMemory
        protected set

    var maxMemory = _maxMemory
        protected set

    var playerCount = _playerCount
        protected set

    var maxPlayerCount = _maxPlayerCount
        protected set

    var memoryUsage = _memoryUsage
        protected set

    var cpuUsage = _cpuUsage
        protected set

    var motd = _motd
        protected set

    var hostname = _hostname
        protected set

    val name get() = "$groupName-$id"

    open fun changeState(state: ServiceState) {
        this.state = state
    }

    fun toSnapshot() = serviceSnapshot {
        this.groupName = this@Service.groupName
        this.id = this@Service.id
        this.state = this@Service.state
        this.type = this@Service.type
        this.properties.putAll(this@Service.properties)
        this.hostname = this@Service.hostname
        this.port = this@Service.port
        this.templates.addAll(this@Service.templates.map { it.toSnapshot() })
        this.information = this@Service.information.toSnapshot()
        this.minimumMemory = this@Service.minMemory
        this.maximumMemory = this@Service.maxMemory
        this.playerCount = this@Service.playerCount
        this.maxPlayerCount = this@Service.maxPlayerCount
        this.memoryUsage = this@Service.memoryUsage
        this.cpuUsage = this@Service.cpuUsage
        this.motd = this@Service.motd
    }

    suspend fun shutdown() {
        ShulkerApi.serviceProvider.shutdownService(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Service

        if (id != other.id) return false
        if (port != other.port) return false
        if (minMemory != other.minMemory) return false
        if (maxMemory != other.maxMemory) return false
        if (playerCount != other.playerCount) return false
        if (maxPlayerCount != other.maxPlayerCount) return false
        if (memoryUsage != other.memoryUsage) return false
        if (cpuUsage != other.cpuUsage) return false
        if (groupName != other.groupName) return false
        if (state != other.state) return false
        if (type != other.type) return false
        if (properties != other.properties) return false
        if (templates != other.templates) return false
        if (information != other.information) return false
        if (motd != other.motd) return false
        if (hostname != other.hostname) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + port
        result = 31 * result + minMemory
        result = 31 * result + maxMemory
        result = 31 * result + playerCount
        result = 31 * result + maxPlayerCount
        result = 31 * result + memoryUsage.hashCode()
        result = 31 * result + cpuUsage.hashCode()
        result = 31 * result + groupName.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + properties.hashCode()
        result = 31 * result + templates.hashCode()
        result = 31 * result + information.hashCode()
        result = 31 * result + motd.hashCode()
        result = 31 * result + hostname.hashCode()
        return result
    }

    override fun toString(): String {
        return "Service(minMemory=$minMemory, maxMemory=$maxMemory, playerCount=$playerCount, maxPlayerCount=$maxPlayerCount, memoryUsage=$memoryUsage, cpuUsage=$cpuUsage, motd='$motd', hostname='$hostname', name='$name', information=$information, templates=$templates, port=$port, properties=$properties, type=$type, state=$state, id=$id, groupName='$groupName')"
    }

    companion object {
        fun fromSnapshot(snapshot: ServiceSnapshot) = Service(
            groupName = snapshot.groupName,
            id = snapshot.id,
            state = snapshot.state,
            type = snapshot.type,
            properties = snapshot.propertiesMap,
            _hostname = snapshot.hostname,
            port = snapshot.port,
            templates = snapshot.templatesList.map { Template.fromSnapshot(it) },
            information = ServiceInformation.fromSnapshot(snapshot.information),
            _minMemory = snapshot.minimumMemory,
            _maxMemory = snapshot.maximumMemory,
            _playerCount = snapshot.playerCount,
            _maxPlayerCount = snapshot.maxPlayerCount,
            _memoryUsage = snapshot.memoryUsage,
            _cpuUsage = snapshot.cpuUsage,
            _motd = snapshot.motd,
        )
    }
}