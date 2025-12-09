package dev.slne.surf.shulker.core.player

import dev.slne.surf.shulker.api.player.ShulkerPlayer
import kotlinx.serialization.Contextual
import java.util.*

class AbstractShulkerPlayer(
    uuid: @Contextual UUID,
    name: String,
    currentServiceName: String
) : ShulkerPlayer(uuid, name, currentServiceName)