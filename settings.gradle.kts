pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://reposilite.slne.dev/releases")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.slne.surf.api.gradle.settings") version "+"
}

rootProject.name = "surf-shulker"

include(":surf-shulker-api")
include(":surf-shulker-core")
include(":surf-shulker-api:surf-shulker-api-client")
include(":surf-shulker-api:surf-shulker-api-common")
include(":surf-shulker-api:surf-shulker-api-client:surf-shulker-api-client-common")
include(":surf-shulker-core:surf-shulker-core-common")
include(":surf-shulker-api:surf-shulker-api-client:surf-shulker-api-client-paper")
include(":surf-shulker-api:surf-shulker-api-client:surf-shulker-api-client-velocity")
include(":surf-shulker-core:surf-shulker-core-client")
include(":surf-shulker-microservice")
include(":surf-shulker-core:surf-shulker-core-client:surf-shulker-core-client-common")
include(":surf-shulker-core:surf-shulker-core-client:surf-shulker-core-client-paper")
include(":surf-shulker-core:surf-shulker-core-client:surf-shulker-core-client-velocity")
include(":surf-shulker-minestom")
include(":surf-shulker-paper")
include(":surf-shulker-velocity")
