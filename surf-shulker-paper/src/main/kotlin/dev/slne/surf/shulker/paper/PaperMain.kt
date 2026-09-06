package dev.slne.surf.shulker.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import org.bukkit.plugin.java.JavaPlugin

class PaperMain : SuspendingJavaPlugin() {

    override suspend fun onEnableAsync() {
        logger.info("surf-shulker enabled")
    }
}

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)