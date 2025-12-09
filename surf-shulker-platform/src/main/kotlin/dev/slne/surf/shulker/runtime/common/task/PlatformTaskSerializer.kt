package dev.slne.surf.shulker.runtime.common.task

import com.google.gson.*
import java.lang.reflect.Type

object PlatformTaskSerializer : JsonSerializer<PlatformTask>, JsonDeserializer<PlatformTask> {
    override fun serialize(
        task: PlatformTask,
        type: Type,
        context: JsonSerializationContext
    ) = JsonPrimitive(task.name)

    override fun deserialize(
        element: JsonElement,
        type: Type,
        context: JsonDeserializationContext
    ) = PlatformTaskPool.findByName(element.asJsonPrimitive.asString)
        ?: throw JsonParseException("Unknown platform task: ${element.asJsonPrimitive.asString}")
}