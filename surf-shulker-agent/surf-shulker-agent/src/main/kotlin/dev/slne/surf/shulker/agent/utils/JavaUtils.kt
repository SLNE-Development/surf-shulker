package dev.slne.surf.shulker.agent.utils

import java.io.File

object JavaUtils {
    fun isValidJavaPath(path: String): Boolean {
        val javaExecutable = if (System.getProperty("os.name").startsWith("Windows")) {
            File(path, "bin/java.exe")
        } else {
            File(path, "bin/java")
        }

        return javaExecutable.exists() && javaExecutable.canExecute()
    }
}