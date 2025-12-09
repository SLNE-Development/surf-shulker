package dev.slne.surf.shulker.agent.event

import dev.slne.surf.shulker.agent.Agent
import dev.slne.surf.shulker.api.event.Event
import dev.slne.surf.shulker.proto.event.*
import dev.slne.surf.surfapi.core.api.util.logger
import io.grpc.stub.ServerCallStreamObserver
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.*

object EventGrpcService : EventControllerGrpc.EventControllerImplBase() {
    private val log = logger()
    private val eventScope =
        CoroutineScope(SupervisorJob() + CoroutineName("event-scope") + CoroutineExceptionHandler { context, throwable ->
            log.atSevere().withCause(throwable)
                .log("Exception in coroutine %s", context[CoroutineName])
        })

    override fun subscribe(
        request: SubscribeEventRequest,
        responseObserver: StreamObserver<EventContext>
    ) {
        val observer = responseObserver as ServerCallStreamObserver<EventContext>

        observer.setOnCancelHandler {
            eventScope.launch {
                Agent.eventProvider.detach(request.eventName, request.serviceName)
            }
        }

        eventScope.launch {
            Agent.eventProvider.attach(
                request.eventName,
                request.serviceName,
                observer
            )
        }
    }

    override fun callEvent(
        request: EventContext,
        responseObserver: StreamObserver<CallEventResponse>
    ) {
        if (isCallCancelled(responseObserver)) return

        safeRespond(responseObserver, processEvent(request))
    }

    private fun processEvent(
        request: EventContext
    ): CallEventResponse {
        return try {
            val fqcn = request.eventFqcn
            val eventClass = Class.forName(fqcn)
            val eventObj =
                Agent.eventProvider.gsonSerializer.fromJson(request.eventData, eventClass) as Event

            Agent.eventProvider.call(eventObj)

            callEventResponse {
                success = true
                message = "Event processed successfully."
            }
        } catch (e: Exception) {
            callEventResponse {
                success = false
                message = "Failed to process event: ${e.message}"
            }
        }
    }

    private fun isCallCancelled(observer: StreamObserver<*>): Boolean {
        return observer is ServerCallStreamObserver && observer.isCancelled
    }

    private fun safeRespond(
        responseObserver: StreamObserver<CallEventResponse>,
        response: CallEventResponse
    ) {
        if (!isCallCancelled(responseObserver)) {
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        }
    }

    override fun unsubscribe(
        request: SubscribeEventRequest,
        responseObserver: StreamObserver<UnsubscribeEventResponse>
    ) {
        eventScope.launch {
            Agent.eventProvider.detach(request.eventName, request.serviceName)

            val response = unsubscribeEventResponse {
                success = true
                message = "Unsubscribed successfully."
            }

            if (!isCallCancelled(responseObserver)) {
                responseObserver.onNext(response)
                responseObserver.onCompleted()
            }
        }
    }
}