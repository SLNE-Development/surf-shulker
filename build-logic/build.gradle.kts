plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // Use version catalog in build-logic
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    implementation(libs.protobuf.gradle)
}