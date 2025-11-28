package dev.slne.surf.shulker.grpc.client.auth

import dev.slne.surf.shulker.grpc.common.config.GRPC_HEADER_TOKEN
import io.grpc.*

class BasicAuthClientInterceptor(
    private val token: String
) : ClientInterceptor {
    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel
    ) = object : ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
        next.newCall(
            method,
            callOptions
        )
    ) {
        override fun start(responseListener: Listener<RespT>, headers: Metadata) {
            headers.put(GRPC_HEADER_TOKEN, token)

            super.start(responseListener, headers)
        }
    }
}