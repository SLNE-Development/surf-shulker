package dev.slne.surf.shulker.controller

import Chat
import ChatServiceGrpc
import chatMessage
import io.grpc.stub.StreamObserver

object ChatService : ChatServiceGrpc.ChatServiceImplBase() {
    private val clients = mutableListOf<StreamObserver<Chat.ChatMessage>>()

    override fun chat(responseObserver: StreamObserver<Chat.ChatMessage>): StreamObserver<Chat.ChatMessage> {
        synchronized(clients) {
            clients.add(responseObserver)
        }

        return object : StreamObserver<Chat.ChatMessage> {
            override fun onNext(msg: Chat.ChatMessage) {
                synchronized(clients) {
                    clients.forEach { client ->
                        client.onNext(chatMessage {
                            sender = msg.sender
                            text = "[${msg.sender}]: ${msg.text}"
                        })
                    }
                }
            }

            override fun onError(exception: Throwable) {
                synchronized(clients) {
                    clients.remove(responseObserver)
                }
            }

            override fun onCompleted() {
                synchronized(clients) {
                    clients.remove(responseObserver)
                }
            }
        }
    }
}