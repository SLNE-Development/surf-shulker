package dev.slne.surf.shulker.grpc.client.config

import dev.slne.surf.shulker.grpc.common.config.GrpcCredentialsConfig
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment
import org.spongepowered.configurate.objectmapping.meta.Setting

@ConfigSerializable
data class GrpcClientConfig(
    val host: String,
    val port: Int,

    @Setting("credentials")
    val credentialsConfig: GrpcCredentialsConfig,

    val userAgent: String = "SurfShulkerClient/1.0",

    @param:Comment("Maximum inbound message size in bytes")
    val maxInboundMessageSize: Int? = null,

    @param:Comment("Maximum inbound metadata size in bytes")
    val maxInboundMetadataSize: Int? = null,

    @param:Comment("When the client should send keep alive pings in milliseconds")
    val keepAliveTime: Long? = null,

    @param:Comment("Timeout for keep alive ping in milliseconds")
    val keepAliveTimeout: Long? = null,

    @param:Comment("Timeout for idle connection in milliseconds")
    val idleTimeout: Long? = null,

    @param:Comment("Maximum number of retry attempts for a call")
    val maxRetryAttempts: Int? = null,
)