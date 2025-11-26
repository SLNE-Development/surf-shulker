package dev.slne.surf.shulker.node.docker.console.command.commands

import dev.slne.surf.shulker.node.common.Node
import dev.slne.surf.shulker.node.docker.console.command.Command

class ConnectCommand(
    private val node: Node
) : Command("connect") {
    override suspend fun execute(args: List<String>) {
        node.connect()
    }
}