package dev.slne.surf.shulker.runtime.common

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

object PlatformVersionSerializer : JsonDeserializer<PlatformVersion> {
    override fun deserialize(
        element: JsonElement,
        type: Type,
        context: JsonDeserializationContext
    ): PlatformVersion {
        val data = element.asJsonObject
        val version = data["version"]?.asJsonPrimitive?.asString
            ?: throw NullPointerException("version field is required to deserialize platform version")

        val additionalProperties = hashMapOf<String, JsonElement>()

        data.keySet().toList().filter { it != "version" }.forEach {
            additionalProperties[it] = data[it]
        }

        return PlatformVersion(
            version = version,
            additionalProperties = additionalProperties
        )
    }
}