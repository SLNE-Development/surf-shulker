package dev.slne.surf.shulker.agent

import java.lang.instrument.Instrumentation

fun main(args: Array<String>) {
    // Clear the screen
    println("\u001b[H\u001b[2J")

    System.setProperty("shulker.lifecycle.boot.start", System.currentTimeMillis().toString())

    registerShutdownHook()

    Thread.currentThread().uncaughtExceptionHandler =
        Thread.UncaughtExceptionHandler { thread, throwable ->
            println("Uncaught exception in thread ${thread.name}")
            throwable.printStackTrace()
        }

    Agent.init()
}

fun premain(agentArgs: Array<String>, instrumentation: Instrumentation) {

}