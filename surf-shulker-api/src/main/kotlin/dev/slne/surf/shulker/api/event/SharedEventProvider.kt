package dev.slne.surf.shulker.api.event

import kotlin.reflect.KClass

abstract class SharedEventProvider {
    abstract fun call(event: Event)
    abstract fun <E : Event> subscribe(eventType: KClass<E>, listener: EventCallback<E>)
}

inline fun <reified E : Event> SharedEventProvider.subscribe(listener: EventCallback<E>) =
    subscribe(E::class, listener)