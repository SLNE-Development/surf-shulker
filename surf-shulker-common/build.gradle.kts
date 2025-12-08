plugins {
    id("dev.slne.surf.surfapi.gradle.core")
    id("exclude-kotlin")
}

dependencies {
    api(project(":surf-shulker-spring"))
    api(project(":surf-shulker-proto"))
    api(libs.kaml)
}