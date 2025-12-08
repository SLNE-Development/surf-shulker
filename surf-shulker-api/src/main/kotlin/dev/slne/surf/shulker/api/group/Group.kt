package dev.slne.surf.shulker.api.group

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import dev.slne.surf.shulker.api.ShulkerKeys
import dev.slne.surf.shulker.api.platform.PlatformIndex
import dev.slne.surf.shulker.api.property.PropertyHolder
import dev.slne.surf.shulker.api.template.Template
import dev.slne.surf.shulker.proto.group.GroupSnapshot
import dev.slne.surf.shulker.proto.group.groupSnapshot
import kotlinx.serialization.Contextual
import java.time.OffsetDateTime

open class Group(
    val name: String,
    private val _minMemory: Int,
    private val _maxMemory: Int,
    private val _minOnlineServices: Int,
    private val _maxOnlineServices: Int,
    val platformIndex: PlatformIndex,
    private val _percentageToStartNewService: Double,
    val createdAt: @Contextual OffsetDateTime,
    val templates: List<Template>,
    val properties: PropertyHolder
) {
    var minMemory: Int = _minMemory
        protected set

    var maxMemory: Int = _maxMemory
        protected set

    var minOnlineServices: Int = _minOnlineServices
        protected set

    var maxOnlineServices: Int = _maxOnlineServices
        protected set

    var percentageToStartNewService: Double = _percentageToStartNewService
        protected set

    fun toSnapshot() = groupSnapshot {
        this.name = this@Group.name
        this.minimumMemory = this@Group.minMemory
        this.maximumMemory = this@Group.maxMemory
        this.minimumOnline = this@Group.minOnlineServices
        this.maximumOnline = this@Group.maxOnlineServices
        this.platform = this@Group.platformIndex.toSnapshot()
        this.percentageToNewService = this@Group.percentageToStartNewService
        this.createdAt = this@Group.createdAt.toString()
        this.templates.addAll(this@Group.templates.map { it.toSnapshot() })
        this.properties.putAll(this@Group.properties.all().mapValues { it.value.toString() })
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Group

        if (minMemory != other.minMemory) return false
        if (maxMemory != other.maxMemory) return false
        if (minOnlineServices != other.minOnlineServices) return false
        if (maxOnlineServices != other.maxOnlineServices) return false
        if (percentageToStartNewService != other.percentageToStartNewService) return false
        if (name != other.name) return false
        if (platformIndex != other.platformIndex) return false
        if (createdAt != other.createdAt) return false
        if (templates != other.templates) return false
        if (properties != other.properties) return false

        return true
    }

    override fun hashCode(): Int {
        var result = minMemory
        result = 31 * result + maxMemory
        result = 31 * result + minOnlineServices
        result = 31 * result + maxOnlineServices
        result = 31 * result + percentageToStartNewService.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + platformIndex.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + templates.hashCode()
        result = 31 * result + properties.hashCode()
        return result
    }

    override fun toString(): String {
        return "Group(minMemory=$minMemory, maxMemory=$maxMemory, minOnlineServices=$minOnlineServices, maxOnlineServices=$maxOnlineServices, percentageToStartNewService=$percentageToStartNewService, properties=$properties, templates=$templates, createdAt=$createdAt, platform=$platformIndex, name='$name')"
    }

    companion object {
        fun fromSnapshot(snapshot: GroupSnapshot) = Group(
            name = snapshot.name,
            _minMemory = snapshot.minimumMemory,
            _maxMemory = snapshot.maximumMemory,
            _minOnlineServices = snapshot.minimumOnline,
            _maxOnlineServices = snapshot.maximumOnline,
            platformIndex = PlatformIndex.fromSnapshot(snapshot.platform),
            _percentageToStartNewService = snapshot.percentageToNewService,
            createdAt = OffsetDateTime.parse(snapshot.createdAt),
            templates = snapshot.templatesList.map { Template.fromSnapshot(it) },
            properties = snapshot.propertiesMap.run {
                val propertyHolder = PropertyHolder.empty()

                forEach { (key, value) ->
                    val primitive = when {
                        value.lowercase()
                            .toBooleanStrictOrNull() != null -> JsonPrimitive(value.toBoolean())

                        value.toIntOrNull() != null -> JsonPrimitive(value.toInt())
                        value.toDoubleOrNull() != null -> JsonPrimitive(value.toDouble())
                        value.toFloatOrNull() != null -> JsonPrimitive(value.toFloat())
                        else -> JsonPrimitive(value)
                    }

                    propertyHolder.raw(key, primitive)
                }

                return@run propertyHolder
            }
        )
    }
}

fun Group.toJson() = JsonObject().apply {
    addProperty(ShulkerKeys.NAME, name)
    addProperty(ShulkerKeys.MIN_MEMORY, minMemory)
    addProperty(ShulkerKeys.MAX_MEMORY, maxMemory)
    addProperty(ShulkerKeys.MIN_ONLINE, minOnlineServices)
    addProperty(ShulkerKeys.MAX_ONLINE, maxOnlineServices)
    addProperty(ShulkerKeys.START_THRESHOLD, percentageToStartNewService)
    addProperty(ShulkerKeys.CREATED_AT, createdAt.toString())
    add(ShulkerKeys.PLATFORM, JsonObject().apply {
        addProperty(ShulkerKeys.NAME, platformIndex.name)
        addProperty(ShulkerKeys.VERSION, platformIndex.version)
    })
    add(ShulkerKeys.TEMPLATES, JsonArray().apply {
        templates.forEach { add(it.name) }
    })
    add(ShulkerKeys.PROPERTIES, JsonObject().apply {
        properties.all().forEach { (key, value) -> add(key, value) }
    })
}