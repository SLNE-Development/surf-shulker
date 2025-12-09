package dev.slne.surf.shulker.api.information

import kotlinx.serialization.Serializable

@Serializable
data class StatAggregate(
    var cpuSum: Double = 0.0,
    var memorySum: Double = 0.0,
    var count: Int = 0
) {
    val avgCpu get() = if (count == 0) 0.0 else cpuSum / count
    val avgMemory get() = if (count == 0) 0.0 else memorySum / count
}