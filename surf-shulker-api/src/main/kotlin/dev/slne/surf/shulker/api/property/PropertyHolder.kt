package dev.slne.surf.shulker.api.property

import com.google.gson.GsonBuilder
import com.google.gson.JsonPrimitive
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import it.unimi.dsi.fastutil.objects.Object2ObjectMap

private val propertySerializer = GsonBuilder().serializeNulls().create()

open class PropertyHolder(
    private val properties: Object2ObjectMap<String, JsonPrimitive> = mutableObject2ObjectMapOf()
) {
    fun <T> hasProperty(property: Property<T>) = properties.contains(property.name)

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(property: Property<T>): T? {
        val jsonPrimitive = properties[property.name] ?: return null

        return when {
            jsonPrimitive.isBoolean -> jsonPrimitive.asBoolean as T
            jsonPrimitive.isNumber -> {
                val number = jsonPrimitive.asNumber

                when {
                    number is Int || number.toDouble() == number.toInt()
                        .toDouble() -> number.toInt() as T

                    else -> number as T
                }
            }

            jsonPrimitive.isString -> jsonPrimitive.asString as T
            else -> null
        }
    }

    fun <T> with(property: Property<T>, value: T): PropertyHolder {
        properties[property.name] = propertySerializer.toJsonTree(value) as JsonPrimitive
        return this
    }

    fun <T> remove(property: Property<T>): PropertyHolder {
        properties.remove(property.name)
        return this
    }

    fun raw(key: String, value: JsonPrimitive): PropertyHolder {
        properties[key] = value
        return this
    }

    fun all(): Map<String, JsonPrimitive> = properties

    companion object {
        fun empty() = PropertyHolder()
    }
}