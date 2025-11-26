package dev.slne.surf.shulker.api.utils.intent

import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectList
import org.jetbrains.annotations.Unmodifiable
import kotlin.reflect.KClass

private val intentRegistry = requiredService<IntentRegistry>()

interface IntentRegistry {
    val intents: @Unmodifiable ObjectList<KClass<out Intent>>

    fun registerIntent(intent: KClass<out Intent>)

    companion object : IntentRegistry by intentRegistry {
        val INSTANCE get() = intentRegistry
    }
}

inline fun <reified I : Intent> IntentRegistry.registerIntent() = registerIntent(I::class)
