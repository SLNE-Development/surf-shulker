plugins {
    id("dev.slne.surf.surfapi.gradle.core")
    id("exclude-kotlin")
}

dependencies {
    api(project(":surf-shulker-core"))

    api(libs.tomlj)
}