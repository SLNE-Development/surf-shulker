import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    `java-library`
}

val libs = the<LibrariesForLibs>()
dependencies {
    implementation(libs.grpc.okhttp)
}