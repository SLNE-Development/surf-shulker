plugins {
    id("dev.slne.surf.surfapi.gradle.core")
    id("proto")
    id("proto-transport")
}

dependencies {
    api(project(":surf-shulker-node:surf-shulker-node-common"))
}