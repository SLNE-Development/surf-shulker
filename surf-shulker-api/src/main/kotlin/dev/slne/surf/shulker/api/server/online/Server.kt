package dev.slne.surf.shulker.api.server.online

import dev.slne.surf.shulker.api.server.OfflineServer
import dev.slne.surf.shulker.api.server.utils.Sender
import dev.slne.surf.shulker.api.user.list.UserList
import net.kyori.adventure.audience.Audience

interface Server : OfflineServer, Audience, Sender {
    val offlineServer: OfflineServer
    val userList: UserList
}