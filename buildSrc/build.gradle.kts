plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // Use version catalog in build-logic
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    implementation("org.springframework.boot:org.springframework.boot.gradle.plugin:3.5.6")
    implementation(libs.protobuf.gradle)
}