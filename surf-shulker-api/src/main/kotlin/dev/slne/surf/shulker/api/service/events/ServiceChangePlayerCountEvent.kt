package dev.slne.surf.shulker.api.service.events

import dev.slne.surf.shulker.api.event.Event
import dev.slne.surf.shulker.api.service.Service

class ServiceChangePlayerCountEvent(
    val service: Service,
    val previousCount: Int,
    val newCount: Int
) : Event