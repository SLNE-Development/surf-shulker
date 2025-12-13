plugins {
    id("dev.slne.surf.surfapi.gradle.velocity")
    id("exclude-kotlin")
}

dependencies {
    api(project(":surf-shulker-bridge:surf-shulker-bridge-api"))
}

velocityPluginFile {
    main = "dev.slne.surf.shulker.bridge.velocity.VelocityBridge"
    authors = listOf("red", "Ammo")
    version = findProperty("version") as String
}