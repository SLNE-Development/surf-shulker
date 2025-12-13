package dev.slne.surf.shulker.runtime.common

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import dev.slne.surf.shulker.api.utils.os.OS
import dev.slne.surf.shulker.proto.group.GroupType
import dev.slne.surf.shulker.runtime.common.bridge.Bridge
import java.lang.reflect.Type

object PlatformDeserializer : JsonDeserializer<Platform> {
    private fun <K, V> JsonDeserializationContext.deserializeMap(
        element: JsonElement?
    ): Map<K, V> = element.let {
        deserialize(it, object : TypeToken<Map<K, V>>() {}.type)
    } ?: emptyMap()

    private fun <T> JsonDeserializationContext.deserializeList(
        element: JsonElement?
    ): List<T> = element.let {
        deserialize(it, object : TypeToken<List<T>>() {}.type)
    } ?: emptyList()

    private inline fun <reified T> JsonDeserializationContext.deserializeObject(
        element: JsonElement?
    ): T? = element?.let {
        deserialize(it, T::class.java)
    }

    override fun deserialize(
        json: JsonElement,
        type: Type,
        context: JsonDeserializationContext
    ): Platform {
        val obj = json.asJsonObject

        val type = context.deserialize<GroupType>(obj["type"], GroupType::class.java)
        val arguments = context.deserializeList<String>(obj["arguments"])
        val flags = context.deserializeList<String>(obj["flags"])
        val versions = context.deserializeList<PlatformVersion>(obj["versions"])
        val bridge = context.deserializeObject<Bridge>(obj["bridge"])
        val defaultStartPort = obj["defaultStartPort"]?.asInt
        val bridgePath = obj["bridgePath"]?.asString
        val afterPrepareTasks = context.deserializeList<String>(obj["afterPrepareTasks"])
        val afterDownloadTasks = context.deserializeList<String>(obj["afterDownloadTasks"])
        val setFileName = obj["setFileName"]?.asBoolean ?: true
        val osNameMapping = context.deserializeMap<OS, String>(obj["osNameMapping"])
        val archNameMapping = context.deserializeMap<String, String>(obj["archNameMapping"])
        val forwarding = obj["forwarding"]?.let { ServerPlatformForwarding.valueOf(it.asString) }
            ?: ServerPlatformForwarding.MODERN

        return Platform(
            name = obj["name"].asString,
            url = obj["url"].asString,
            shutdownCommand = obj["shutdownCommand"]?.asString ?: "stop",
            type = type,
            arguments = arguments,
            flags = flags,
            versions = versions,
            bridge = bridge,
            defaultStartPort = defaultStartPort,
            bridgePath = bridgePath,
            afterPrepareTasks = afterPrepareTasks,
            afterDownloadTasks = afterDownloadTasks,
            setFileName = setFileName,
            osNameMapping = osNameMapping,
            archNameMapping = archNameMapping,
            forwarding = forwarding
        )
    }
}