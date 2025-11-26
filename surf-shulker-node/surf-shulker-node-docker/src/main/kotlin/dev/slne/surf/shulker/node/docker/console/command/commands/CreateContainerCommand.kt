package dev.slne.surf.shulker.node.docker.console.command.commands

import dev.slne.surf.shulker.node.common.Node
import dev.slne.surf.shulker.node.docker.console.command.Command
import java.util.*

class CreateContainerCommand(
    private val node: Node
) : Command("ccreate") {
    override suspend fun execute(args: List<String>) {
        val uuid = UUID.fromString(args[0])
        val port = args[1].toInt()

        val container = node.createContainer(
            uuid = uuid,
            port = port
        )

        container.create()

        println("Container $uuid created on port $port")
        println("Host: ${container.host}")
        println("Port: ${container.port}")
        println("Memory Limit: ${container.memoryLimit ?: "No limit"}")
        println("CPU Limit: ${container.cpuLimit ?: "No limit"}")
        println(
            "CPU Pinning: ${
                if (container.cpuPinning.isEmpty()) "No pinning" else container.cpuPinning.joinToString(
                    ","
                )
            }"
        )
        println("Persistent Volumes: ${container.persistentVolumes}")
    }
}