plugins {
    id("dev.slne.surf.surfapi.gradle.standalone")
}

dependencies {
    api(libs.bundles.docker)
    api(project(":surf-shulker-node:surf-shulker-node-common"))
}