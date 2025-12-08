package dev.slne.surf.shulker.api.service.events

import dev.slne.surf.shulker.api.event.Event
import dev.slne.surf.shulker.api.service.Service
import dev.slne.surf.shulker.proto.service.ServiceState

class ServiceChangeStateEvent(
    val service: Service,
    val from: ServiceState,
    val to: ServiceState
) : Event