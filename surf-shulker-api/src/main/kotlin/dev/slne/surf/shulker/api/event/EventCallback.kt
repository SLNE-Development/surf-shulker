package dev.slne.surf.shulker.api.event

fun interface EventCallback<E> {
    fun call(event: E)
}