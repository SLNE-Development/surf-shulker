package dev.slne.surf.shulker.runtime.common.metadata

import com.google.gson.JsonObject
import dev.slne.surf.shulker.runtime.common.PLATFORM_GSON
import dev.slne.surf.shulker.runtime.common.PLATFORM_METADATA_URL
import dev.slne.surf.shulker.runtime.common.PLATFORM_PATH
import dev.slne.surf.surfapi.core.api.util.logger
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.util.zip.ZipFile
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.system.exitProcess

object MetadataTranslator {
    private val log = logger()

    fun read() {
        if (!(PLATFORM_PATH.resolve("tasks").exists() && PLATFORM_PATH.resolve("platforms")
                .exists())
        ) {
            if (!copyFromGithub() && !copyFromClassPath()) {
                log.atSevere().log("Could not load platform metadata!")
                exitProcess(-1)
            }
        }
    }

    fun copyFromGithub(): Boolean {
        val metadataUrl = PLATFORM_METADATA_URL + "metadata.json"

        val jsonText = URI(metadataUrl).toURL().openStream().bufferedReader(Charsets.UTF_8)
            .use { it.readText() }
        val jsonContext = PLATFORM_GSON.fromJson(jsonText, JsonObject::class.java)

        val tasks = jsonContext["tasks"]?.asJsonArray ?: return false
        val platforms = jsonContext["platforms"]?.asJsonArray ?: return false

        tasks.forEach { task ->
            val fileName = task.asJsonPrimitive.asString

            downloadAndWriteToLocal("tasks", fileName)
        }

        platforms.forEach { platform ->
            val fileName = platform.asJsonPrimitive.asString

            downloadAndWriteToLocal("platforms", fileName)
        }

        return true
    }

    private fun downloadAndWriteToLocal(type: String, fileName: String) {
        val remoteUrl = "$PLATFORM_METADATA_URL$type/$fileName.json"
        val targetPath = PLATFORM_PATH.resolve(type).resolve("$fileName.json")

        targetPath.parent.createDirectories()

        val content = URI(remoteUrl).toURL().openStream()
            .bufferedReader(Charsets.UTF_8).use { it.readText() }

        Files.writeString(targetPath, content)
    }

    private fun copyComponentFromJar(sourceDir: String, targetSubPath: String) {
        val jarPath = javaClass.classLoader.getResource(sourceDir)!!.path
            .substringBefore("!")
            .removePrefix("file:")

        ZipFile(jarPath).use { zip ->
            zip.entries().asSequence()
                .filter { it.name.startsWith(sourceDir) && !it.isDirectory }
                .forEach { entry ->
                    val inputStream = zip.getInputStream(entry)
                    val relativePath = entry.name.removePrefix("metadata/")
                    val outFile = PLATFORM_PATH.resolve(relativePath).toFile()

                    outFile.parentFile.mkdir()
                    outFile.outputStream().use { out -> inputStream.use { it.copyTo(out) } }
                }
        }
    }

    private fun copyComponentFromDirectory(sourceDir: File, targetSubPath: String) {
        val targetDir = PLATFORM_PATH.resolve(targetSubPath).toFile()

        sourceDir.copyRecursively(targetDir, overwrite = true)
    }

    fun copyFromClassPath(): Boolean {
        val components = listOf("tasks", "platforms")

        for (component in components) {
            val dirPath = "metadata/$component"
            val dirUrl = javaClass.classLoader.getResource(dirPath) ?: continue

            when (dirUrl.protocol) {
                "jar" -> copyComponentFromJar(dirPath, component)
                "file" -> copyComponentFromDirectory(File(dirUrl.toURI()), component)
                else -> continue
            }
        }

        return true
    }
}