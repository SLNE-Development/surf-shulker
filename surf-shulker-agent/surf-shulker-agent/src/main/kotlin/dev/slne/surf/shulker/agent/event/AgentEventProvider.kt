package dev.slne.surf.shulker.agent.event

import dev.slne.surf.shulker.agent.Agent
import dev.slne.surf.shulker.api.event.Event
import dev.slne.surf.shulker.api.event.SharedEventProvider
import dev.slne.surf.shulker.proto.event.EventContext
import io.grpc.stub.ServerCallStreamObserver
import java.util.concurrent.ConcurrentHashMap

class AgentEventProvider : SharedEventProvider {
    private val remoteSubscribers = ConcurrentHashMap<String, MutableList<EventSubscription>>()
    private val localSubscribers = ConcurrentHashMap<String, MutableList<(Event) -> Unit>>()

    fun attach(
        event: String,
        serviceName: String,
        observer: ServerCallStreamObserver<EventContext>
    ) {
        val service = Agent.runtime.serviceStorage.find
    }
}