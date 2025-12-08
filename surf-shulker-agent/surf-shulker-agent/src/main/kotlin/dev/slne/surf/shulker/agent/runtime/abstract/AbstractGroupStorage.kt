package dev.slne.surf.shulker.agent.runtime.abstract

import com.google.gson.GsonBuilder
import dev.slne.surf.shulker.agent.group.AbstractGroup
import dev.slne.surf.shulker.agent.runtime.RuntimeGroupStorage
import dev.slne.surf.shulker.api.property.PropertyHolder
import dev.slne.surf.shulker.api.property.PropertySerializer
import dev.slne.surf.shulker.api.template.Template
import dev.slne.surf.shulker.api.template.TemplateSerializer
import dev.slne.surf.surfapi.core.api.util.logger
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.listDirectoryEntries

abstract class AbstractGroupStorage(val path: Path = Path("local/groups")) : RuntimeGroupStorage {
    private lateinit var cachedGroups: ObjectArrayList<AbstractGroup>

    init {
        this.init()
    }

    private fun init() {
        path.createDirectories()

        cachedGroups = path.listDirectoryEntries("*.json").mapTo(mutableObjectListOf()) {
            return@mapTo STORAGE_GSON.fromJson(Files.readString(it), AbstractGroup::class.java)
        }
    }

    override suspend fun findAll(): List<AbstractGroup> {
        return cachedGroups
    }

    override suspend fun findByName(name: String): AbstractGroup? {
        return cachedGroups.firstOrNull { it.name == name }
    }

    override suspend fun create(group: AbstractGroup): AbstractGroup? {
        if (findByName(group.name) != null) {
            return null
        }

        Files.writeString(groupPath(group), STORAGE_GSON.toJson(group))
        cachedGroups.add(group)

        return group
    }

    override suspend fun update(group: AbstractGroup): AbstractGroup? {
        if (findByName(group.name) == null) {
            return null
        }

        Files.writeString(groupPath(group), STORAGE_GSON.toJson(group))

        val index = cachedGroups.indexOfFirst { it.name == group.name }

        if (index != -1) {
            cachedGroups[index] = group
        }

        return group
    }

    override suspend fun delete(group: AbstractGroup): AbstractGroup? {
        cachedGroups.remove(group)
        groupPath(group).deleteIfExists()

        return group
    }

    private fun groupPath(group: AbstractGroup): Path {
        return path.resolve("${group.name}.json")
    }

    override fun reload() {
        log.atInfo().log("Reloading group storage from disk...")
        init()
        log.atInfo().log("Group storage reloaded.")
    }

    companion object {
        private val log = logger()

        private val STORAGE_GSON = GsonBuilder().setPrettyPrinting()
            .registerTypeHierarchyAdapter(PropertyHolder::class.java, PropertySerializer)
            .registerTypeAdapter(PropertyHolder::class.java, PropertySerializer)
            .registerTypeHierarchyAdapter(Template::class.java, TemplateSerializer)
            .registerTypeAdapter(Template::class.java, TemplateSerializer)
            .create()
    }
}