plugins {
    id("dev.slne.surf.surfapi.gradle.core")
}

dependencies {
    api(project(":surf-shulker-core"))

    api(libs.tomlj)
}