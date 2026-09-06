plugins {
    id("dev.slne.surf.api.gradle.velocity")
}

surfVelocityApi {
    withCoreVelocity()
    withSurfRedis()
}

dependencies {
    api(projects.surfShulkerCore.surfShulkerCoreClient.surfShulkerCoreClientCommon)
    api(projects.surfShulkerApi.surfShulkerApiClient.surfShulkerApiClientVelocity)
}
