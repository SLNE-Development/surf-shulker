package dev.slne.surf.shulker.grpc.server.auth

import dev.slne.surf.shulker.grpc.common.config.GRPC_HEADER_TOKEN
import io.grpc.*

class BasicAuthServerInterceptor(
    private val token: String
) : ServerInterceptor {
    override fun <ReqT : Any, RespT : Any> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        val token = headers.get(GRPC_HEADER_TOKEN)

        if (token != this.token) {
            call.close(
                Status.UNAUTHENTICATED.withDescription("Invalid token"),
                Metadata()
            )

            return object : ServerCall.Listener<ReqT>() {}
        }

        return next.startCall(call, headers)
    }
}