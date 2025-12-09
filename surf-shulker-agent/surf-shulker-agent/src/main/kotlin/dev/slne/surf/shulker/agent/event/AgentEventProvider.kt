package dev.slne.surf.shulker.agent.event

import dev.slne.surf.shulker.agent.Agent
import dev.slne.surf.shulker.api.event.Event
import dev.slne.surf.shulker.api.event.EventCallback
import dev.slne.surf.shulker.api.event.SharedEventProvider
import dev.slne.surf.shulker.api.service.Service
import dev.slne.surf.shulker.proto.event.EventContext
import dev.slne.surf.shulker.proto.event.eventContext
import dev.slne.surf.surfapi.core.api.util.logger
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import io.grpc.stub.ServerCallStreamObserver
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass

object AgentEventProvider : SharedEventProvider() {
    private val log = logger()

    private val remoteSubscribers = ConcurrentHashMap<String, MutableList<EventSubscription>>()
    private val localSubscribers = ConcurrentHashMap<String, MutableList<(Event) -> Unit>>()

    val registeredAmount: Int get() = remoteSubscribers.values.sumOf { it.size }

    suspend fun detach(
        event: String,
        serviceName: String
    ) {
        val service = Agent.runtime.serviceStorage.findByName(serviceName) ?: return

        remoteSubscribers[event]?.removeIf { it.service == service }
    }

    suspend fun attach(
        event: String,
        serviceName: String,
        observer: ServerCallStreamObserver<EventContext>
    ) {
        val service = Agent.runtime.serviceStorage.findByName(serviceName) ?: run {
            log.atWarning().log("Service $serviceName not found for event subscription.")
            observer.onCompleted()
            return
        }

        val subscription = EventSubscription(service, observer)
        remoteSubscribers.computeIfAbsent(event) { CopyOnWriteArrayList() }.add(subscription)

        observer.setOnCancelHandler {
            remoteSubscribers[event]?.remove(subscription)
        }
    }

    fun dropServiceSubscriptions(service: Service) {
        remoteSubscribers.forEach { (_, subscriptions) ->
            subscriptions.removeIf { it.service == service }
        }
    }

    override fun call(event: Event) {
        val eventName = event.javaClass.simpleName

        localSubscribers[eventName]?.forEach { it(event) }

        remoteSubscribers[eventName]?.forEach {
            if (it.sub.isCancelled) {
                it.sub.onNext(
                    eventContext {
                        this.eventName = eventName
                        this.eventData = gsonSerializer.toJson(event)
                    }
                )
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <E : Event> subscribe(
        eventType: KClass<E>,
        listener: EventCallback<E>
    ) {
        val eventName = eventType.simpleName ?: return

        localSubscribers.computeIfAbsent(eventName) { mutableObjectListOf() }
            .add { event -> listener.call(event as E) }
    }
}