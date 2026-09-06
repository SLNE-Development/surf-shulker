plugins {
    id("dev.slne.surf.api.gradle.velocity")
}

surfVelocityApi {
    mainClass("dev.slne.surf.shulker.paper.PaperMain")
    withCoreVelocity()
    withSurfRedis()
}

dependencies {
    api(projects.surfShulkerCore.surfShulkerCoreClient.surfShulkerCoreClientVelocity)
}
