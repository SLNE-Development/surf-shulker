plugins {
    id("dev.slne.surf.surfapi.gradle.core")
}

dependencies {
    api(project(":surf-shulker-api"))
    api(project(":surf-shulker-proto"))
    api(project(":surf-shulker-grpc:surf-shulker-grpc-server"))

    api(project(":surf-shulker-runtime:surf-shulker-runtime-local"))
    api(project(":surf-shulker-runtime:surf-shulker-runtime-docker"))
    api(project(":surf-shulker-runtime:surf-shulker-runtime-kubernetes"))

    api(libs.bundles.jline)
    api(libs.bundles.docker)
}

tasks.jar {
    archiveFileName.set("shulker-agent-$version.jar")

    manifest {
        attributes["Main-Class"] = "dev.slne.surf.shulker.agent.AgentBootKt"
        attributes["Premain-Class"] = "dev.slne.surf.shulker.agent.AgentBootKt"
    }
}