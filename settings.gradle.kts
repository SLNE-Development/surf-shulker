plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}


rootProject.name = "surf-shulker"

val map = mapOf(
    "surf-shulker-bom" to "surf-shulker-bom",

    // Api
    "surf-shulker-api" to "surf-shulker-api",
    "surf-shulker-spring" to "surf-shulker-spring",

    // Controller
    "surf-shulker-controller" to "surf-shulker-controller",

    // GRPC
    "surf-shulker-proto" to "surf-shulker-proto",
    "surf-shulker-grpc:surf-shulker-grpc-common" to "surf-shulker-grpc-common",
    "surf-shulker-grpc:surf-shulker-grpc-server" to "surf-shulker-grpc-server",
    "surf-shulker-grpc:surf-shulker-grpc-client" to "surf-shulker-grpc-client",

    // Runtime
    "surf-shulker-runtime:surf-shulker-runtime-common" to "surf-shulker-runtime-common",
    "surf-shulker-runtime:surf-shulker-runtime-paper" to "surf-shulker-runtime-paper",
    "surf-shulker-runtime:surf-shulker-runtime-velocity" to "surf-shulker-runtime-velocity",

    // Agent
    "surf-shulker-agent:surf-shulker-agent" to "surf-shulker-agent",
    "surf-shulker-agent:surf-shulker-agent-launcher" to "surf-shulker-agent-launcher",

    // Node
    "surf-shulker-node:surf-shulker-node-common" to "surf-shulker-node-common",
    "surf-shulker-node:surf-shulker-node-registry" to "surf-shulker-node-registry",
    "surf-shulker-node:surf-shulker-node-docker" to "surf-shulker-node-docker",
)

map.forEach { (project, name) ->
    include(project)
    project(":$project").name = name
}
