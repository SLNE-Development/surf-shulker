package dev.slne.surf.shulker.runtime.common

interface RuntimeLoader {
    fun runnable(): Boolean
    fun instance(): Runtime
}