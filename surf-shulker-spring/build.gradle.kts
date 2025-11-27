plugins {
    id("dev.slne.surf.surfapi.gradle.core")
    id("exclude-kotlin")
}

dependencies {
    api(libs.bundles.spring.api.common)
    api(libs.bundles.spring.aop)
}