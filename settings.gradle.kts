pluginManagement {
    includeBuild("build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}


rootProject.name = "surf-shulker"

val map = mapOf(
    "surf-shulker-api" to "surf-shulker-api",
    "surf-shulker-controller" to "surf-shulker-controller",
    "surf-shulker-runtime:surf-shulker-runtime-paper" to "surf-shulker-runtime-paper",
    "surf-shulker-runtime:surf-shulker-runtime-velocity" to "surf-shulker-runtime-velocity",
    "surf-shulker-docker" to "surf-shulker-docker",
    "surf-shulker-node:surf-shulker-node-common" to "surf-shulker-node-common",
    "surf-shulker-node:surf-shulker-node-docker" to "surf-shulker-node-docker",
    "surf-shulker-template" to "surf-shulker-template"
)

map.forEach { (project, name) ->
    include(project)
    project(":$project").name = name
}