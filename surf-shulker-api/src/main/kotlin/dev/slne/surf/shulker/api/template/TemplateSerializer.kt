package dev.slne.surf.shulker.api.template

import com.google.gson.*
import java.lang.reflect.Type

object TemplateSerializer : JsonSerializer<Template>, JsonDeserializer<Template> {
    override fun serialize(src: Template, type: Type, context: JsonSerializationContext) =
        JsonPrimitive(src.name)

    override fun deserialize(
        json: JsonElement,
        type: Type,
        context: JsonDeserializationContext
    ) = Template(json.asJsonPrimitive.asString)
}