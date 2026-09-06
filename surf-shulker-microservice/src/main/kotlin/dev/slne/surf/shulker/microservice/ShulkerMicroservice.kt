package dev.slne.surf.shulker.microservice

import com.google.auto.service.AutoService
import dev.slne.surf.microservice.api.microservice.Microservice
import kotlin.io.path.Path

@AutoService(Microservice::class)
class ShulkerMicroservice : Microservice() {

    override val dataPath = Path("config")

    override suspend fun onBootstrap(args: List<String>) {
        // Register handlers here.
    }
}
