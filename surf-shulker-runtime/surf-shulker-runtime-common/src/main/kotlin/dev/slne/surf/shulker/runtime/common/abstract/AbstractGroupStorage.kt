package dev.slne.surf.shulker.runtime.common.abstract

import com.google.gson.GsonBuilder
import dev.slne.surf.shulker.api.group.Group
import dev.slne.surf.shulker.api.property.PropertyHolder
import dev.slne.surf.shulker.api.template.Template
import dev.slne.surf.shulker.runtime.common.RuntimeGroupStorage
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
    private lateinit var cachedGroups: ObjectArrayList<Group>

    init {
        this.init()
    }

    private fun init() {
        path.createDirectories()

        cachedGroups = path.listDirectoryEntries("*.json").mapTo(mutableObjectListOf()) {
            return@mapTo STORAGE_GSON.fromJson(Files.readString(it), Group::class.java)
        }
    }

    override suspend fun findAll(): List<Group> {
        return cachedGroups
    }

    override suspend fun findByName(name: String): Group? {
        return cachedGroups.firstOrNull { it.name == name }
    }

    override suspend fun create(group: Group): Group? {
        if (findByName(group.name) != null) {
            return null
        }

        Files.writeString(groupPath(group), STORAGE_GSON.toJson(group))
        cachedGroups.add(group)

        return group
    }

    override suspend fun update(group: Group): Group? {
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

    override suspend fun delete(group: Group): Group? {
        cachedGroups.remove(group)
        groupPath(group).deleteIfExists()

        return group
    }

    private fun groupPath(group: Group): Path {
        return path.resolve("${group.name}.json")
    }

    override suspend fun reload() {
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