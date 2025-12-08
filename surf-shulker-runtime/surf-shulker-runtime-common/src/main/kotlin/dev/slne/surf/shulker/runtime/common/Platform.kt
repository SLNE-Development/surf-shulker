package dev.slne.surf.shulker.runtime.common

import dev.slne.surf.shulker.common.filesystem.copyDirectory
import dev.slne.surf.shulker.common.os.OS
import dev.slne.surf.shulker.common.os.currentCpuArchitecture
import dev.slne.surf.shulker.common.os.currentOs
import dev.slne.surf.shulker.proto.group.GroupType
import dev.slne.surf.shulker.runtime.common.bridge.Bridge
import dev.slne.surf.shulker.runtime.common.bridge.BridgeType
import dev.slne.surf.shulker.runtime.common.exception.PlatformCacheMissingException
import dev.slne.surf.shulker.runtime.common.exception.PlatformVersionInvalidException
import dev.slne.surf.shulker.runtime.common.task.PlatformTaskPool
import dev.slne.surf.surfapi.core.api.util.toObjectList
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.*

class Platform(
    val name: String,
    val url: String,
    val shutdownCommand: String = "stop",
    val type: GroupType,
    val arguments: List<String> = emptyList(),
    val flags: List<String> = emptyList(),
    val versions: List<PlatformVersion>,
    val bridge: Bridge? = null,
    val defaultStartPort: Int?,
    private val bridgePath: String? = null,
    private val afterPrepareTasks: List<String> = emptyList(),
    private val afterDownloadTasks: List<String> = emptyList(),
    private val setFileName: Boolean = true,
    private val osNameMapping: Map<OS, String> = emptyMap(),
    private val archNameMapping: Map<String, String> = emptyMap(),
    val forwarding: ServerPlatformForwarding = ServerPlatformForwarding.MODERN
) {
    suspend fun prepare(
        servicePath: Path,
        version: String,
        environment: PlatformParameters
    ) {
        val path = cachePath(version)
        val version = this.version(version)

        environment["filename"] = path.fileName

        if (version == null) {
            throw PlatformVersionInvalidException()
        }

        if (!path.exists()) {
            throw PlatformCacheMissingException()
        }

        tasks.forEach { it.runTask(servicePath, environment) }
        copyDirectory(path.parent, servicePath)

        if (bridge == null) {
            return
        }

        if (!bridge.isDownloaded) {
            bridge.download()
        }

        if (bridge.type == BridgeType.ON_PREMISE) {
            val targetBridge = servicePath.resolve("$bridgePath/${bridge.path.name}")
            targetBridge.createParentDirectories()
            Files.copy(bridge.path, targetBridge, StandardCopyOption.REPLACE_EXISTING)
            return
        }

        if (bridge.type == BridgeType.OFF_PREMISE) {
            val bridgeClass = Class.forName(bridge.bridgeClass)
            val constructor = bridgeClass.getDeclaredConstructor(
                Path::class.java,
                String::class.java,
                Int::class.java,
                String::class.java
            )

            constructor.newInstance(
                servicePath,
                environment["server-name"],
                environment.get<Int>("agent_port"),
                environment["filename"]
            )
        }
    }

    @OptIn(ExperimentalPathApi::class)
    suspend fun cachePrepare(version: String, environment: PlatformParameters) {
        val cachePath = cachePath(version)
        cachePath.createParentDirectories()

        try {
            val version = this.version(version) ?: throw PlatformVersionInvalidException()

            var replacedUrl = url.replace("{{version}}", version.version)
                .replace("{{arch}}", archDownloadName)
                .replace("{{os}}", osDownloadName)

            version.additionalProperties.forEach { (key, value) ->
                replacedUrl = replacedUrl.replace("{{$key}}", value.asJsonPrimitive.asString)
            }

            val downloadFile =
                if (setFileName) cachePath.toFile() else cachePath.parent.resolve("download")
                    .toFile()

            URI(replacedUrl).toURL().openStream().use { inputStream ->
                downloadFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            downloadTasks.forEach { it.runTask(cachePath, environment) }
        } catch (ex: Exception) {
            cachePath.parent.deleteRecursively()
            throw ex
        }
    }

    fun cacheExists(version: String?) = version?.let { cachePath(it).exists() } == true

    fun cachePath(version: String) = Path("local/metadata/cache/$name/$version/$name-$version")
    fun version(version: String) = versions.firstOrNull { it.version == version }

    val tasks
        get() = afterPrepareTasks.mapNotNull { PlatformTaskPool.findByName(it) }.toObjectList()

    val downloadTasks
        get() = afterDownloadTasks.mapNotNull { PlatformTaskPool.findByName(it) }.toObjectList()

    private val osDownloadName get() = osNameMapping.getOrElse(currentOs) { currentOs.name }
    private val archDownloadName get() = archNameMapping.getOrElse(currentCpuArchitecture) { currentCpuArchitecture }
}