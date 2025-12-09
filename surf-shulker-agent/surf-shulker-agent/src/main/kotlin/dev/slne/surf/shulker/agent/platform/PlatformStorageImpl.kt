package dev.slne.surf.shulker.agent.platform

import dev.slne.surf.shulker.api.platform.Platform
import dev.slne.surf.shulker.api.platform.PlatformVersion
import dev.slne.surf.shulker.api.platform.SharedPlatformProvider
import dev.slne.surf.shulker.proto.group.GroupType
import dev.slne.surf.shulker.runtime.common.PlatformPool
import dev.slne.surf.surfapi.core.api.util.toObjectList
import it.unimi.dsi.fastutil.objects.ObjectList

object PlatformStorageImpl : SharedPlatformProvider<Platform> {
    override suspend fun findAll(): ObjectList<Platform> {
        return PlatformPool.platformPool.map {
            Platform(
                it.name,
                it.type,
                it.versions.map { version -> PlatformVersion(version.version) }
            )
        }.toObjectList()
    }

    override suspend fun findByName(name: String): Platform? {
        return PlatformPool.platformPool.firstOrNull { it.name == name }?.let {
            Platform(
                it.name,
                it.type,
                it.versions.map { version -> PlatformVersion(version.version) }
            )
        }
    }

    override suspend fun findByType(type: GroupType): ObjectList<Platform> {
        return PlatformPool.platformPool.filter { it.type == type }.map {
            Platform(
                it.name,
                it.type,
                it.versions.map { version -> PlatformVersion(version.version) }
            )
        }.toObjectList()
    }
}