package dev.slne.surf.shulker.controller

import Test
import TestServiceGrpcKt
import message
import shuffledMessage

object TestService : TestServiceGrpcKt.TestServiceCoroutineImplBase() {
    override suspend fun shuffleMessage(request: Test.Message) = shuffledMessage {
        shuffledMessage = request.message.toList().shuffled().joinToString("")
    }

    override suspend fun printMessage(request: Test.Message) = message {
        message = "Server received: ${request.message}"
        result = Test.SendResult.SUCCESS
    }
}
