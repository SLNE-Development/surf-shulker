package dev.slne.surf.shulker.runtime.common

import com.google.gson.JsonElement

data class PlatformVersion(
    val version: String,
    val additionalProperties: Map<String, JsonElement> = emptyMap()
) {
    val buildId: String? get() = additionalProperties["buildId"]?.asJsonPrimitive?.asString
    val requiredRuntimeVersion: String? get() = additionalProperties["requiredRuntimeVersion"]?.asJsonPrimitive?.asString

    operator fun get(key: String) = additionalProperties[key]

    inline operator fun <reified T> get(key: String): T? {
        val element = additionalProperties[key] ?: return null

        return when (T::class) {
            String::class -> element.asJsonPrimitive.asString
            Boolean::class -> element.asJsonPrimitive.asBoolean
            Int::class -> element.asJsonPrimitive.asInt
            Double::class -> element.asJsonPrimitive.asDouble
            Float::class -> element.asJsonPrimitive.asFloat
            Long::class -> element.asJsonPrimitive.asLong
            JsonElement::class -> element
            
            else -> {
                null
            }
        } as? T
    }
}