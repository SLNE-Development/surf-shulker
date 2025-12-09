package dev.slne.surf.shulker.api.event

import com.google.gson.GsonBuilder
import dev.slne.surf.shulker.api.player.PlayerSerializer
import dev.slne.surf.shulker.api.player.ShulkerPlayer
import dev.slne.surf.shulker.api.service.Service
import dev.slne.surf.shulker.api.service.ServiceSerializer
import dev.slne.surf.shulker.api.template.Template
import dev.slne.surf.shulker.api.template.TemplateSerializer
import kotlin.reflect.KClass

abstract class SharedEventProvider {
    val gsonSerializer = GsonBuilder()
        .registerTypeHierarchyAdapter(Service::class.java, ServiceSerializer)
        .registerTypeHierarchyAdapter(Template::class.java, TemplateSerializer)
        .registerTypeHierarchyAdapter(ShulkerPlayer::class.java, PlayerSerializer)
        .create()

    abstract fun call(event: Event)

    abstract fun <E : Event> subscribe(eventType: KClass<E>, listener: EventCallback<E>)
}

inline fun <reified E : Event> SharedEventProvider.subscribe(listener: EventCallback<E>) =
    subscribe(E::class, listener)