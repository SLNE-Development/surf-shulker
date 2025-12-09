package dev.slne.surf.shulker.runtime.common

import dev.slne.surf.shulker.api.group.Group
import dev.slne.surf.shulker.api.group.SharedGroupProvider
import dev.slne.surf.shulker.api.utils.Reloadable

interface RuntimeGroupStorage : SharedGroupProvider<Group>, Reloadable {
    override suspend fun reload() {}
}