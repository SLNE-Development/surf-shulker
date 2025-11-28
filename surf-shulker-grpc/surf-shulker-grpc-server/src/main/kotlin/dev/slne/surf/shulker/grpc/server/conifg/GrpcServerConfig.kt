package dev.slne.surf.shulker.grpc.server.conifg

import dev.slne.surf.shulker.grpc.common.config.GrpcCredentialsConfig
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment
import org.spongepowered.configurate.objectmapping.meta.Setting

@ConfigSerializable
data class GrpcServerConfig(
    val port: Int,

    @Setting("credentials")
    val credentialsConfig: GrpcCredentialsConfig,

    @param:Comment("Handshake timeout in milliseconds")
    val handshakeTimeout: Long? = null,

    @param:Comment("Timeout configurations in milliseconds")
    val keepAliveTime: Long? = null,

    @param:Comment("Keep alive timeout in milliseconds")
    val keepAliveTimeout: Long? = null,

    @param:Comment("Maximum connection idle time in milliseconds")
    val maxConnectionIdle: Long? = null,

    @param:Comment("Maximum connection age in milliseconds")
    val maxConnectionAge: Long? = null,

    @param:Comment("Grace period before forceful shutdown in milliseconds")
    val maxConnectionAgeGrace: Long? = null,

    @param:Comment("Permit keep alive time in milliseconds")
    val permitKeepAliveTime: Long? = null,

    @param:Comment("Permit keep alive without calls")
    val permitKeepAliveWithoutCalls: Boolean? = null,

    @param:Comment("Maximum inbound message size in bytes")
    val maxInboundMessageSize: Long? = null,

    @param:Comment("Maximum inbound metadata size in bytes")
    val maxInboundMetadataSize: Long? = null
)