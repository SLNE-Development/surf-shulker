package dev.slne.surf.shulker.node.docker.console.command.commands

import dev.slne.surf.shulker.node.common.Node
import dev.slne.surf.shulker.node.docker.console.command.Command
import java.util.*

class KillContainerCommand(
    val node: Node
) : Command("ckill") {
    override suspend fun execute(args: List<String>) {
        val containerUuid = UUID.fromString(args[0])
        val container = node.findContainerByUuid(containerUuid) ?: run {
            println("Container $containerUuid not found")
            return
        }

        container.kill()
    }
}