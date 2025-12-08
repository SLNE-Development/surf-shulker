package dev.slne.surf.shulker.agent.runtime

import dev.slne.surf.shulker.agent.service.AbstractService

interface RuntimeExpender<out S : AbstractService> {
    fun executeCommand(service: @UnsafeVariance S, command: String): Boolean

    fun readLogs(service: @UnsafeVariance S, lines: Int = 100): List<String>
}