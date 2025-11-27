package dev.slne.surf.shulker.controller

import Chat
import ChatServiceGrpcKt
import chatMessage
import io.grpc.Grpc
import io.grpc.InsecureChannelCredentials
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

val flow = MutableSharedFlow<Chat.ChatMessage>()

fun main() {
    val client = Grpc.newChannelBuilderForAddress(
        "localhost",
        40000,
        InsecureChannelCredentials.create()
    ).defaultLoadBalancingPolicy("round_robin").build()

    val stub = ChatServiceGrpcKt.ChatServiceCoroutineStub(client)

    CoroutineScope(Dispatchers.IO).launch {
        val scanner = Scanner(System.`in`)

        while (true) {
            print("Enter message: ")

            flow.emit(chatMessage {
                sender = "Client-${UUID.randomUUID()}"
                text = scanner.nextLine()
            })
        }
    }

    runBlocking {
        stub.chat(flow).collect { msg ->
            println(msg)
        }

        client.shutdown()
    }
}