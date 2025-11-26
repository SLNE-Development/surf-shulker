@file:OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package dev.slne.surf.shulker.node.docker.console

import dev.slne.surf.shulker.node.common.Node
import dev.slne.surf.shulker.node.docker.console.command.Command
import dev.slne.surf.shulker.node.docker.console.command.commands.*
import kotlinx.coroutines.*
import java.util.*

class Console(
    node: Node
) {
    private val scanner = Scanner(System.`in`)
    private val commands = mutableListOf<Command>()

    private val thread = newSingleThreadContext("ConsoleThread")

    private val consoleScope = CoroutineScope(
        SupervisorJob() +
                thread +
                CoroutineExceptionHandler { context, throwable ->
                    println("Error in console coroutine context $context")
                    throwable.printStackTrace()
                } +
                CoroutineName("DockerNode-Console-Scope")
    )

    init {
        commands.add(ConnectCommand(node))
        commands.add(DisconnectCommand(node))
        commands.add(CreateContainerCommand(node))
        commands.add(StartContainerCommand(node))
        commands.add(StopContainerCommand(node))
        commands.add(KillContainerCommand(node))
        commands.add(DeleteContainerCommand(node))
    }

    fun start() {
        consoleScope.launch {
            tick()
        }
    }

    fun stop() {
        consoleScope.cancel("Console stopped")
        close()
    }

    private suspend fun tick() {
        println("Console started. Type 'help' for a list of commands.")
        while (true) {
            print("> ")
            if (hasNextLine()) {
                val line = nextLine()
                val parts = line.split(" ")
                val commandName = parts[0]
                val args = parts.drop(1)

                val command = commands.find { it.name == commandName }

                if (command != null) {
                    command.execute(args)
                } else {
                    println("Unknown command: $commandName")
                }
            }
        }
    }

    fun hasNextLine(): Boolean {
        return scanner.hasNextLine()
    }

    fun nextLine(): String {
        return scanner.nextLine()
    }

    fun close() {
        scanner.close()
    }
}