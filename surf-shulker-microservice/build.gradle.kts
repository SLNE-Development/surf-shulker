plugins {
    id("dev.slne.surf.api.gradle.standalone")
    id("dev.slne.surf.microservice")
}

surfStandaloneApi {
    withSurfDatabaseR2dbc("+", "dev.slne.surf.shulker.libs")
}

dependencies {
    api(projects.surfShulkerCore.surfShulkerCoreCommon)
}

surfMicroservice {
    withMicroserviceApi()
}
