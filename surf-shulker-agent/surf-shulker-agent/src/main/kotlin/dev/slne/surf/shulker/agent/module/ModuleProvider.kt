package dev.slne.surf.shulker.agent.module

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import dev.slne.surf.shulker.api.module.LoadedModule
import dev.slne.surf.shulker.api.module.ShulkerModule
import dev.slne.surf.shulker.api.module.ShulkerModuleInfo
import dev.slne.surf.shulker.api.utils.Reloadable
import dev.slne.surf.surfapi.core.api.util.logger
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import java.io.File
import java.net.URLClassLoader
import java.nio.file.Files
import java.util.jar.JarFile
import kotlin.io.path.Path
import kotlin.io.path.notExists

object ModuleProvider : Reloadable {
    private val log = logger()

    private val modulePath = Path("local/modules")
    private val loadedModules = mutableObjectListOf<LoadedModule>()

    init {
        if (modulePath.notExists()) {
            Files.createDirectories(modulePath)
        }
    }

    override suspend fun reload() {
        unloadModules()
        loadModules()
        enableModules()
    }

    suspend fun loadModules() {
        val (successful, failed) = jarFiles.mapNotNull { file ->
            val metadata = file.readMetadata() ?: return@mapNotNull null

            runCatching {
                loadModuleFromFile(file, metadata).also {
                    it.module.onLoad()
                }
            }.fold(
                onSuccess = {
                    metadata.moduleName to true
                },
                onFailure = {
                    log.atSevere().withCause(it)
                        .log("Failed to load module ${metadata.id} from file ${file.name}.")
                    metadata.moduleName to false
                }
            )
        }.partition { it.second }

        val status = successful.map { "&3${it.first}" } + failed.map { "&c${it.first}" }

        if (status.isNotEmpty()) {
            log.atInfo().log("Modules loaded with status: ${status.joinToString(", ")}")
        }
    }

    suspend fun enableModules() {
        loadedModules.forEach { module ->
            runCatching {
                module.module.onEnable()
            }.onSuccess {
                log.atInfo().log("Module ${module.metadata.id} enabled successfully.")
            }.onFailure {
                log.atSevere().withCause(it).log("Failed to enable module ${module.metadata.id}.")
            }
        }
    }

    suspend fun unloadModules() {
        loadedModules.forEach { module ->
            runCatching {
                module.module.onDisable()
                module.classLoader.close()
            }.onSuccess {
                log.atInfo().log("Module ${module.metadata.id} unloaded successfully.")
            }.onFailure {
                log.atSevere().withCause(it).log("Failed to unload module ${module.metadata.id}.")
            }
        }

        loadedModules.clear()
    }

    private fun loadModuleFromFile(file: File, metadata: ShulkerModuleInfo): LoadedModule {
        val classLoader =
            URLClassLoader(arrayOf(file.toURI().toURL()), this::class.java.classLoader)
        val clazz = classLoader.loadClass(metadata.main)

        require(ShulkerModule::class.java.isAssignableFrom(clazz)) {
            "Main class ${metadata.main} does not implement ShulkerModule interface"
        }

        val instance = clazz.getDeclaredConstructor().newInstance() as ShulkerModule

        return LoadedModule(
            instance,
            classLoader,
            metadata
        ).also {
            loadedModules.add(it)
        }
    }

    private fun File.readMetadata(): ShulkerModuleInfo? = runCatching {
        JarFile(this).use { jar ->
            val entry = jar.getJarEntry("module.yml") ?: return null

            jar.getInputStream(entry).use { stream ->
                Yaml.default.decodeFromStream(ShulkerModuleInfo.serializer(), stream)
            }
        }
    }.onFailure {
        throw IllegalStateException("Failed to read module metadata from $name", it)
    }.getOrNull()

    private val jarFiles
        get() = modulePath.toFile().listFiles { _, name -> name.endsWith(".jar") }?.toList()
            .orEmpty()
}