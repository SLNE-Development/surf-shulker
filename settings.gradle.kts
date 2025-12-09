plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}


rootProject.name = "surf-shulker"

val map = mapOf(
    "surf-shulker-bom" to "surf-shulker-bom",

    // Api
    "surf-shulker-api" to "surf-shulker-api",
    "surf-shulker-core" to "surf-shulker-core",
    "surf-shulker-platform" to "surf-shulker-platform",
    "surf-shulker-spring" to "surf-shulker-spring",

    // GRPC
    "surf-shulker-proto" to "surf-shulker-proto",
    "surf-shulker-grpc:surf-shulker-grpc-common" to "surf-shulker-grpc-common",
    "surf-shulker-grpc:surf-shulker-grpc-server" to "surf-shulker-grpc-server",
    "surf-shulker-grpc:surf-shulker-grpc-client" to "surf-shulker-grpc-client",

    // Runtime
    "surf-shulker-runtime:surf-shulker-runtime-common" to "surf-shulker-runtime-common",
    "surf-shulker-runtime:surf-shulker-runtime-local" to "surf-shulker-runtime-local",
    "surf-shulker-runtime:surf-shulker-runtime-docker" to "surf-shulker-runtime-docker",
    "surf-shulker-runtime:surf-shulker-runtime-kubernetes" to "surf-shulker-runtime-kubernetes",

    // Agent
    "surf-shulker-agent:surf-shulker-agent" to "surf-shulker-agent",
    "surf-shulker-agent:surf-shulker-agent-launcher" to "surf-shulker-agent-launcher",
)

map.forEach { (project, name) ->
    include(project)
    project(":$project").name = name
}
