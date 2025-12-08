package dev.slne.surf.shulker.runtime.common

import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf

class PlatformParameters(
    private val version: PlatformVersion?
) {
    val parameters = mutableObject2ObjectMapOf<String, Any>()
    val versionPrefix = "version_"

    inline operator fun <reified T> get(key: String) = this.parameters[key] as? T
    operator fun set(key: String, value: Any) {
        this.parameters[key] = value
    }

    fun modifyValueWithEnvironment(value: String): String {
        var modifiedValue = value

        parameters.forEach { (key, value) ->
            modifiedValue = modifiedValue.replace("{{$key}}", value.toString())
        }

        if (modifiedValue.contains("{{$versionPrefix}}") && version != null) {
            modifiedValue = modifiedValue.replace(versionPrefix, "")
                .replace("{{version}}", version.version)

            version.additionalProperties.forEach { (key, value) ->
                modifiedValue = modifiedValue.replace("{{$key}}", value.asJsonPrimitive.asString)
            }
        }

        return modifiedValue
    }
}