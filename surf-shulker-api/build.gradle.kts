plugins {
    id("dev.slne.surf.surfapi.gradle.core")
    id("exclude-kotlin")
}

dependencies {
    api(project(":surf-shulker-proto"))
    api(project(":surf-shulker-runtime:surf-shulker-runtime-common"))

    api(libs.kaml)
}