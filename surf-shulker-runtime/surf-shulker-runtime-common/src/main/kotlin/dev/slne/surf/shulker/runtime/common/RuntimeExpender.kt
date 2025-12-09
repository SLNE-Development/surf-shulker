package dev.slne.surf.shulker.runtime.common

import dev.slne.surf.shulker.api.service.Service
import it.unimi.dsi.fastutil.objects.ObjectList

interface RuntimeExpender<out S : Service> {
    suspend fun executeCommand(service: @UnsafeVariance S, command: String): Boolean

    suspend fun readLogs(service: @UnsafeVariance S, lines: Int = 100): ObjectList<String>
}