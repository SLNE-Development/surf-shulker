package dev.slne.surf.shulker.controller

import dev.slne.surf.shulker.controller.proto.test.Message
import dev.slne.surf.shulker.controller.proto.test.TestServiceGrpcKt
import dev.slne.surf.shulker.controller.proto.test.shuffledMessage

object TestService : TestServiceGrpcKt.TestServiceCoroutineImplBase() {
    override suspend fun shuffleMessage(request: Message) = shuffledMessage {
        shuffledMessage = request.message.toList().shuffled().joinToString("")
    }
}
