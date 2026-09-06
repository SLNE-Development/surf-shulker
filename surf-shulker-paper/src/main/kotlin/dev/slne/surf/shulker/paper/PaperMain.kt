package dev.slne.surf.shulker.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin

class PaperMain : SuspendingJavaPlugin() {

    override suspend fun onEnableAsync() {
        logger.info("surf-shulker enabled")
    }
}
