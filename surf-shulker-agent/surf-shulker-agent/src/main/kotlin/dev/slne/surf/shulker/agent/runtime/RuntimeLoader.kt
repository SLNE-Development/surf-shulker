package dev.slne.surf.shulker.agent.runtime

interface RuntimeLoader {
    fun runnable(): Boolean
    fun instance(): Runtime
}