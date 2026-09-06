package dev.slne.surf.shulker.minestom

import com.google.inject.Inject
import com.google.inject.Singleton
import dev.slne.minestom.lobby.api.plugin.MinestomPluginEntrypoint
import dev.slne.minestom.lobby.api.plugin.annotation.DataDirectory
import java.nio.file.Path

@Singleton
class ShulkerMinestomEntrypoint @Inject constructor(
    @DataDirectory path: Path
) : MinestomPluginEntrypoint {

    init {
        dataPath = path
    }

    override suspend fun start() {
        // Start here.
    }

    companion object {
        @Volatile
        lateinit var dataPath: Path
            private set
    }
}
