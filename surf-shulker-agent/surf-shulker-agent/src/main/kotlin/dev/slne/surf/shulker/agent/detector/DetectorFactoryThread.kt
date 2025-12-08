package dev.slne.surf.shulker.agent.detector

import dev.slne.surf.surfapi.core.api.util.logger

class DetectorFactoryThread(
    detector: Detector
) {
    private val log = logger()

    private val thread = Thread {
        while (true) {
            try {
                detector.tick()
                Thread.sleep(detector.cycleLife)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            } catch (e: Exception) {
                log.atSevere().withCause(e)
                    .log("An error occurred in detector thread")
            }
        }
    }

    fun start() {
        thread.start()
    }

    fun stop() {
        thread.interrupt()
    }
}