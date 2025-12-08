package dev.slne.surf.shulker.agent.detector

interface Detector {
    val cycleLife: Long

    fun tick()
}