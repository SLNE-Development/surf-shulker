package dev.slne.surf.shulker.common.json

import com.google.gson.*
import com.google.gson.internal.Streams
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf

class RuntimeTypeAdapterFactory<T> private constructor(
    private val baseType: Class<T>,
    private val typeFieldName: String,
    private val maintainType: Boolean
) : TypeAdapterFactory {
    private val labelToSubtype = mutableObject2ObjectMapOf<String, Class<out T>>()
    private val subtypeToLabel = mutableObject2ObjectMapOf<Class<out T>, String>()

    fun registerSubtype(
        subtype: Class<out T>,
        label: String = subtype.simpleName
    ): RuntimeTypeAdapterFactory<T> {
        if (subtypeToLabel.containsKey(subtype) || labelToSubtype.containsKey(label)) {
            throw IllegalArgumentException("Subtype or label already registered: $subtype, $label")
        }

        labelToSubtype[label] = subtype
        subtypeToLabel[subtype] = label

        return this
    }

    inline fun <reified S : T> registerSubtype(
        label: String = S::class.java.simpleName
    ): RuntimeTypeAdapterFactory<T> = registerSubtype(S::class.java, label)

    override fun <R : Any?> create(gson: Gson, type: TypeToken<R>): TypeAdapter<R>? {
        val rawType = type.rawType as Class<R>

        if (!baseType.isAssignableFrom(rawType)) return null

        val labelToDelegate = mutableObject2ObjectMapOf<String, TypeAdapter<*>>()
        val subtypeToDelegate = mutableObject2ObjectMapOf<Class<out T>, TypeAdapter<*>>()

        labelToSubtype.forEach { (label, subtype) ->
            val delegate = gson.getDelegateAdapter(this, TypeToken.get(subtype))

            labelToDelegate[label] = delegate
            subtypeToDelegate[subtype] = delegate
        }

        return object : TypeAdapter<R>() {
            @Suppress("UNCHECKED_CAST")
            override fun read(reader: JsonReader): R {
                val jsonElement = Streams.parse(reader)
                val jsonObject = jsonElement.asJsonObject
                val labelJsonElement = jsonObject.remove(typeFieldName) ?: throw JsonParseException(
                    "Cannot deserialize $baseType because it does not contain a field named $typeFieldName"
                )
                val label = labelJsonElement.asString
                val subType = labelToSubtype[label]
                    ?: throw JsonParseException("Cannot deserialize $baseType subtype named $label; did you forget to register it?")
                val delegate = labelToDelegate[label] as TypeAdapter<R>

                return delegate.fromJsonTree(jsonObject)
            }

            @Suppress("UNCHECKED_CAST")
            override fun write(writer: JsonWriter, value: R) {
                val srcType = value!!::class.java as Class<out T>
                val label = subtypeToLabel[srcType]
                    ?: throw JsonParseException("Cannot serialize unknown subtype: $srcType")
                val delegate = subtypeToDelegate[srcType] as TypeAdapter<R>
                val jsonObject = delegate.toJsonTree(value).asJsonObject

                if (!maintainType) {
                    val clone = JsonObject()
                    clone.add(typeFieldName, JsonPrimitive(label))

                    jsonObject.entrySet().forEach { (key, value) ->
                        clone.add(key, value)
                    }

                    Streams.write(clone, writer)
                } else {
                    jsonObject.add(typeFieldName, JsonPrimitive(label))

                    Streams.write(jsonObject, writer)
                }
            }
        }
    }

    companion object {
        inline fun <reified T> of(
            typeFieldName: String = "type",
            maintainType: Boolean = false
        ) = of(T::class.java, typeFieldName, maintainType)

        fun <T> of(
            baseType: Class<T>,
            typeFieldName: String = "type",
            maintainType: Boolean = false
        ) =
            RuntimeTypeAdapterFactory(baseType, typeFieldName, maintainType)
    }
}