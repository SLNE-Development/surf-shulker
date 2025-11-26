package dev.slne.surf.shulker.controller

import dev.slne.surf.shulker.controller.proto.test.TestServiceGrpcKt
import dev.slne.surf.shulker.controller.proto.test.message
import io.grpc.Grpc
import io.grpc.InsecureChannelCredentials
import kotlinx.coroutines.runBlocking

fun main() {
    val client = Grpc.newChannelBuilderForAddress(
        "localhost",
        40000,
        InsecureChannelCredentials.create()
    ).defaultLoadBalancingPolicy("round_robin").build()

    val stub = TestServiceGrpcKt.TestServiceCoroutineStub(client)
    val request = message {
        message = "Hello from client!"
    }

    runBlocking {
        val response = stub.shuffleMessage(request)
        println("Received response from server: ${response.shuffledMessage}")

        client.shutdown()
    }
}