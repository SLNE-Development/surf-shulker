@file:OptIn(InternalSerializationApi::class)

package dev.slne.surf.shulker.api.utils.config

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import java.io.InputStream
import kotlin.reflect.KClass

fun <C : Any> InputStream.readYml(config: KClass<C>): C {
    val yml = bufferedReader(Charsets.UTF_8).use { it.readText() }

    return Yaml.default.decodeFromString(config.serializer(), yml)
}

inline fun <reified C : Any> InputStream.readYml(): C = readYml(C::class)