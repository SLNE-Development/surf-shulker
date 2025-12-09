package dev.slne.surf.shulker.api.service

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import dev.slne.surf.shulker.api.ShulkerKeys
import dev.slne.surf.shulker.api.template.Template
import dev.slne.surf.shulker.proto.group.GroupType
import dev.slne.surf.shulker.proto.service.ServiceState
import java.lang.reflect.Type

object ServiceSerializer : JsonDeserializer<Service>, JsonSerializer<Service> {
    override fun deserialize(
        json: JsonElement,
        type: Type,
        context: JsonDeserializationContext
    ): Service {
        val data = json.asJsonObject

        val name = data.get(ShulkerKeys.NAME).asString
        val id = data.get(ShulkerKeys.ID).asInt
        val hostname = data.get(ShulkerKeys.HOSTNAME).asString
        val port = data.get(ShulkerKeys.PORT).asInt
        val type = GroupType.valueOf(data.get(ShulkerKeys.TYPE).asString)
        val state = ServiceState.valueOf(data.get(ShulkerKeys.STATE).asString)
        val templatesType = object : TypeToken<List<Template>>() {}.type
        val templates =
            context.deserialize<List<Template>>(data.get(ShulkerKeys.TEMPLATES), templatesType)
        val information = ServiceInformation.fromJson(data.get(ShulkerKeys.INFORMATION).asString)
        val minMemory = data.get(ShulkerKeys.MIN_MEMORY).asInt
        val maxMemory = data.get(ShulkerKeys.MAX_MEMORY).asInt
        val maxPlayerCount = data.get(ShulkerKeys.MAX_PLAYER_COUNT).asInt
        val playerCount = data.get(ShulkerKeys.PLAYER_COUNT).asInt
        val memoryUsage = data.get(ShulkerKeys.MEMORY_USAGE).asDouble
        val cpuUsage = data.get(ShulkerKeys.CPU_USAGE).asDouble
        val motd = data.get(ShulkerKeys.MOTD).asString
        val propertiesType = object : TypeToken<Map<String, String>>() {}.type
        val properties = context.deserialize<Map<String, String>>(
            data.get(ShulkerKeys.PROPERTIES),
            propertiesType
        )

        return Service(
            name,
            id,
            state,
            type,
            properties,
            hostname,
            port,
            templates,
            information,
            minMemory,
            maxMemory,
            playerCount,
            maxPlayerCount,
            memoryUsage,
            cpuUsage,
            motd
        )
    }

    override fun serialize(
        service: Service,
        type: Type,
        context: JsonSerializationContext
    ) = JsonObject().apply {
        addProperty(ShulkerKeys.NAME, service.name)
        addProperty(ShulkerKeys.ID, service.id)
        addProperty(ShulkerKeys.HOSTNAME, service.hostname)
        addProperty(ShulkerKeys.PORT, service.port)
        addProperty(ShulkerKeys.STATE, service.state.name)
        addProperty(ShulkerKeys.TYPE, service.type.name)
        addProperty(ShulkerKeys.INFORMATION, service.information.toString())
        addProperty(ShulkerKeys.MIN_MEMORY, service.minMemory)
        addProperty(ShulkerKeys.MAX_MEMORY, service.maxMemory)
        addProperty(ShulkerKeys.MAX_PLAYER_COUNT, service.maxPlayerCount)
        addProperty(ShulkerKeys.PLAYER_COUNT, service.playerCount)
        addProperty(ShulkerKeys.MEMORY_USAGE, service.memoryUsage)
        addProperty(ShulkerKeys.CPU_USAGE, service.cpuUsage)
        addProperty(ShulkerKeys.MOTD, service.motd)
        add(ShulkerKeys.TEMPLATES, context.serialize(service.templates))
        add(
            ShulkerKeys.PROPERTIES,
            context.serialize(service.properties.map { it.key to it.value }.toMap())
        )
    }
}