package dev.slne.surf.shulker.grpc.server

import dev.slne.surf.shulker.grpc.server.auth.BasicAuthServerInterceptor
import dev.slne.surf.shulker.grpc.server.conifg.GrpcServerConfig
import io.grpc.BindableService
import io.grpc.Grpc
import io.grpc.InsecureServerCredentials
import io.grpc.Server
import java.util.concurrent.TimeUnit

class ShulkerGrpcServer(
    config: GrpcServerConfig,
    services: List<BindableService>
) {
    private val builder = Grpc.newServerBuilderForPort(
        config.port,
        InsecureServerCredentials.create()
    )

    init {
        with(builder) {
            config.handshakeTimeout?.let { handshakeTimeout(it, TimeUnit.MILLISECONDS) }
            config.keepAliveTime?.let { keepAliveTime(it, TimeUnit.MILLISECONDS) }
            config.keepAliveTimeout?.let { keepAliveTimeout(it, TimeUnit.MILLISECONDS) }
            config.maxConnectionIdle?.let { maxConnectionIdle(it, TimeUnit.MILLISECONDS) }
            config.maxConnectionAge?.let { maxConnectionAge(it, TimeUnit.MILLISECONDS) }
            config.maxConnectionAgeGrace?.let { maxConnectionAgeGrace(it, TimeUnit.MILLISECONDS) }
            config.permitKeepAliveTime?.let { permitKeepAliveTime(it, TimeUnit.MILLISECONDS) }
            config.permitKeepAliveWithoutCalls?.let { permitKeepAliveWithoutCalls(it) }
            config.maxInboundMessageSize?.let { maxInboundMessageSize(it.toInt()) }
            config.maxInboundMetadataSize?.let { maxInboundMetadataSize(it.toInt()) }

            val credentialsConfig = config.credentialsConfig
            intercept(BasicAuthServerInterceptor(credentialsConfig.token))

            services.forEach {
                addService(it)
            }
        }
    }

    var server: Server? = null
        private set

    fun start() {
        server = builder.build()
        server?.start()
    }

    fun stop() {
        server?.shutdown()
        server = null
    }
}