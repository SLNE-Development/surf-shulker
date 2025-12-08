package dev.slne.surf.shulker.agent.runtime.abstract

import dev.slne.surf.shulker.agent.group.AbstractGroup
import dev.slne.surf.shulker.agent.runtime.RuntimeGroupStorage
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import kotlinx.io.files.Path
import kotlin.io.path.createDirectories

abstract class AbstractGroupStorage(val path: Path = Path("local/groups")) : RuntimeGroupStorage {
    private lateinit var cachedGroups: ObjectArrayList<AbstractGroup>

    init {
        this.init()
    }

    private fun init() {
        path.createDirectories()

        TODO("Cache groups")
    }

    override suspend fun findAll(): List<AbstractGroup> {


    }
}