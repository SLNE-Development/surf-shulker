package dev.slne.surf.shulker.controller

import io.grpc.*

fun main(args: Array<String>) {
    val port = args[0].toInt()

    val server = Grpc.newServerBuilderForPort(
        port,
        InsecureServerCredentials.create()
    )
        .intercept(object : ServerInterceptor {
            override fun <ReqT : Any?, RespT : Any?> interceptCall(
                call: ServerCall<ReqT?, RespT?>?,
                headers: Metadata?,
                next: ServerCallHandler<ReqT?, RespT?>?
            ): ServerCall.Listener<ReqT?>? {
                println("Received call to method: ${call?.methodDescriptor?.fullMethodName}")
                return next?.startCall(call, headers)
            }
        })
        .addService(TestService)
        .build()

    server.start()
    println("Server started on port ${server.port}")
    server.awaitTermination()
}