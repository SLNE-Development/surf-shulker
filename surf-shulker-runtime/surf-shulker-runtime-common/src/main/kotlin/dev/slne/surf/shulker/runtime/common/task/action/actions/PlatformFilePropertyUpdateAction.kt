package dev.slne.surf.shulker.runtime.common.task.action.actions

import dev.slne.surf.shulker.runtime.common.PlatformParameters
import dev.slne.surf.shulker.runtime.common.task.PlatformTaskStep
import dev.slne.surf.shulker.runtime.common.task.TaskFileMode
import dev.slne.surf.shulker.runtime.common.task.action.PlatformAction
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.FileInputStream
import java.nio.file.Path
import java.util.*
import kotlin.io.path.*

class PlatformFilePropertyUpdateAction(
    private val key: String,
    private val value: String,
    private val fileMode: TaskFileMode = TaskFileMode.EVERY
) : PlatformAction() {
    override suspend fun run(file: Path, step: PlatformTaskStep, environment: PlatformParameters) {
        if (fileMode == TaskFileMode.IF_EXISTS && file.notExists()) {
            return
        }

        if (fileMode == TaskFileMode.IF_NOT_EXISTS && file.exists()) {
            return
        }

        file.parent.createDirectories()

        val translatedValue = environment.modifyValueWithEnvironment(value)
        val parsedValue = parseValue(translatedValue)

        when {
            step.fileName.endsWith(".properties") -> handleProperties(file, parsedValue.toString())
            step.fileName.endsWith(".yml") -> handleYaml(file, parsedValue)
            step.fileName.endsWith(".yaml") -> handleYaml(file, parsedValue)
            step.fileName.endsWith(".toml") -> handleToml(file, parsedValue.toString())
            else -> throw IllegalArgumentException("Unsupported file type for property update: ${step.fileName}")
        }
    }

    private fun handleProperties(file: Path, value: String) {
        val properties = Properties().apply {
            if (file.exists()) {
                FileInputStream(file.toFile()).use { load(it) }
            }

            setProperty(key, value)
        }

        file.outputStream().use { outputStream ->
            properties.store(outputStream, "Updated configuration property: $key")
        }
    }

    @Suppress("TYPE_INTERSECTION_AS_REIFIED_WARNING")
    private fun handleYaml(file: Path, value: Any?) {
        val loader = YamlConfigurationLoader.builder().path(file).build()
        val root = if (file.exists()) loader.load() else loader.createNode()
        val path = key.split(".").map { it.toIntOrNull() ?: it }.toTypedArray()

        root.node(*path).set(value)
        loader.save(root)
    }

    private fun handleToml(file: Path, value: Any?) {
        val content =
            if (file.exists()) file.readText().lines().toMutableList() else mutableListOf()
        val lineIndex = content.indexOfFirst { it.trim().startsWith("$key =") }

        if (lineIndex >= 0) {
            content[lineIndex] = "$key = \"$value\""
        } else {
            content.add("$key = \"$value\"")
        }

        if (file.notExists()) {
            file.createFile()
        }

        file.writeText(content.joinToString("\n"))
    }

    private fun parseValue(input: String): Any = when {
        input.equals("true", ignoreCase = true) -> true
        input.equals("false", ignoreCase = true) -> false
        input.toIntOrNull() != null -> input.toInt()
        input.toDoubleOrNull() != null -> input.toDouble()
        else -> input
    }
}