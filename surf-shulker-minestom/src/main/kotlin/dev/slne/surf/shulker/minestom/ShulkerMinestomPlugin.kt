package dev.slne.surf.shulker.minestom

import com.google.auto.service.AutoService
import dev.slne.minestom.lobby.api.plugin.MinestomPlugin
import dev.slne.minestom.lobby.api.plugin.annotation.MinestomPluginMeta

@AutoService(MinestomPlugin::class)
@MinestomPluginMeta(
    "surf-shulker-minestom",
    dependsOn = ["surf-api-minestom"]
)
class ShulkerMinestomPlugin : MinestomPlugin(ShulkerMinestomEntrypoint::class.java) {

    override fun configurePlugin() {
        // Bind event and command registrars here.
    }
}
