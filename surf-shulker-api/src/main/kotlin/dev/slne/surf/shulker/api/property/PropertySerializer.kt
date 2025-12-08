package dev.slne.surf.shulker.api.property

import com.google.gson.*
import java.lang.reflect.Type

object PropertySerializer : JsonSerializer<PropertyHolder>, JsonDeserializer<PropertyHolder> {
    override fun serialize(
        src: PropertyHolder,
        type: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val json = JsonObject()

        src.all().forEach { (key, value) ->
            json.add(key, value)
        }

        return json
    }

    override fun deserialize(
        json: JsonElement,
        type: Type,
        context: JsonDeserializationContext
    ): PropertyHolder {
        val holder = PropertyHolder.empty()

        json.asJsonObject.entrySet().forEach { (key, value) ->
            when {
                value.isJsonPrimitive -> holder.raw(key, value.asJsonPrimitive)
                value.isJsonNull -> holder.raw(key, JsonPrimitive("null"))
                value.isJsonObject || value.isJsonArray -> holder.raw(
                    key,
                    JsonPrimitive(value.toString())
                )
            }
        }

        return holder
    }
}