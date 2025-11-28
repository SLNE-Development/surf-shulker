package dev.slne.surf.shulker.grpc.common.config

import io.grpc.Metadata
import org.spongepowered.configurate.objectmapping.ConfigSerializable

val GRPC_HEADER_TOKEN: Metadata.Key<String> = Metadata.Key.of(
    "token",
    Metadata.ASCII_STRING_MARSHALLER
)

@ConfigSerializable
data class GrpcCredentialsConfig(
    val token: String = "",
)