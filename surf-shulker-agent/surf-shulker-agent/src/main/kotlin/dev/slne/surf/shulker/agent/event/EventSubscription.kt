package dev.slne.surf.shulker.agent.event

import dev.slne.surf.shulker.api.service.Service
import dev.slne.surf.shulker.proto.event.EventContext
import io.grpc.stub.ServerCallStreamObserver

data class EventSubscription(
    val service: Service,
    val sub: ServerCallStreamObserver<EventContext>
)
