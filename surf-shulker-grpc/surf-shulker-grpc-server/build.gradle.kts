plugins {
    id("dev.slne.surf.surfapi.gradle.core")
    id("proto")
}

dependencies {
    api(project(":surf-shulker-grpc:surf-shulker-grpc-common"))
}