package dev.slne.surf.shulker.agent.runtime

import dev.slne.surf.shulker.agent.group.AbstractGroup
import dev.slne.surf.shulker.api.group.SharedGroupProvider

interface RuntimeGroupStorage : SharedGroupProvider<AbstractGroup>, Reloadable {
    override fun reload() {}
}