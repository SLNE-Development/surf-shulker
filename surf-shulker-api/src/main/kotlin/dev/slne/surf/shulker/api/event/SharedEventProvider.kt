package dev.slne.surf.shulker.api.event

import kotlin.reflect.KClass

interface SharedEventProvider {
    suspend fun call(event: Event)

    suspend fun <E : Event> subscribe(eventType: KClass<E>, listener: suspend (E) -> Any)
}

suspend inline fun <reified E : Event> SharedEventProvider.subscribe(noinline listener: suspend (E) -> Any) =
    subscribe(E::class, listener)