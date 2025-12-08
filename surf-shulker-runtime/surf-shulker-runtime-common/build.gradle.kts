plugins {
    id("dev.slne.surf.surfapi.gradle.core")
}

dependencies {
    api(project(":surf-shulker-proto"))
    api(project(":surf-shulker-common"))
    api(project(":surf-shulker-spring"))

    api(libs.tomlj)
}