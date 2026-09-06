plugins {
    id("dev.slne.surf.api.gradle.paper-raw")
}

surfRawPaperApi {
    withSurfRedis()
}

dependencies {
    api(projects.surfShulkerCore.surfShulkerCoreClient.surfShulkerCoreClientCommon)
    api(projects.surfShulkerApi.surfShulkerApiClient.surfShulkerApiClientPaper)
}
