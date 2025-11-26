package dev.slne.surf.shulker.node.docker.console.command.commands

import dev.slne.surf.shulker.node.common.Node
import dev.slne.surf.shulker.node.docker.console.command.Command

class DisconnectCommand(
    private val node: Node
) : Command("disconnect") {
    override suspend fun execute(args: List<String>) {
        node.disconnect()
    }
}