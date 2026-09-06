plugins {
    id("dev.slne.surf.api.gradle.minestom")
}

surfMinestomApi {
    withCoreMinestom()
    withSurfRedis()
}

dependencies {
    api(projects.surfShulkerCore.surfShulkerCoreClient.surfShulkerCoreClientCommon)
}
