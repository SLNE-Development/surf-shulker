package dev.slne.surf.shulker.grpc.client

import dev.slne.surf.shulker.grpc.client.auth.BasicAuthClientInterceptor
import dev.slne.surf.shulker.grpc.client.config.GrpcClientConfig
import io.grpc.Grpc
import io.grpc.InsecureChannelCredentials
import io.grpc.ManagedChannel
import java.util.concurrent.TimeUnit

class ShulkerGrpcClient(
    config: GrpcClientConfig
) {
    private val clientBuilder = Grpc.newChannelBuilderForAddress(
        config.host,
        config.port,
        InsecureChannelCredentials.create()
    )

    init {
        with(clientBuilder) {
            config.maxInboundMessageSize?.let { maxInboundMessageSize(it) }
            config.maxInboundMetadataSize?.let { maxInboundMetadataSize(it) }
            config.keepAliveTime?.let { keepAliveTime(it, TimeUnit.MILLISECONDS) }
            config.keepAliveTimeout?.let { keepAliveTimeout(it, TimeUnit.MILLISECONDS) }
            config.idleTimeout?.let { idleTimeout(it, TimeUnit.MILLISECONDS) }
            config.maxRetryAttempts?.let { maxRetryAttempts(it) }

            val credentialsConfig = config.credentialsConfig
            intercept(BasicAuthClientInterceptor(credentialsConfig.token))
        }
    }

    var client: ManagedChannel? = null
        private set

    fun connect() {
        if (isConnected()) error("Already connected")

        client = clientBuilder.build()
    }

    fun disconnect() {
        client?.shutdown()
        client = null
    }

    fun isConnected() = client != null && !client!!.isShutdown
}