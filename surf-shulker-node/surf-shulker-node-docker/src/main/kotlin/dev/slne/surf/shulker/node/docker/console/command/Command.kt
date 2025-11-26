package dev.slne.surf.shulker.node.docker.console.command

abstract class Command(
    val name: String
) {
    abstract suspend fun execute(args: List<String>)
}