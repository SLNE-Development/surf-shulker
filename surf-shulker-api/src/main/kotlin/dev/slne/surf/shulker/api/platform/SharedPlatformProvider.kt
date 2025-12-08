package dev.slne.surf.shulker.api.platform

import dev.slne.surf.shulker.proto.group.GroupType
import it.unimi.dsi.fastutil.objects.ObjectList

interface SharedPlatformProvider<P : Platform> {
    suspend fun findAll(): ObjectList<P>

    suspend fun findByName(name: String): P?

    suspend fun findByType(type: GroupType): ObjectList<P>
}