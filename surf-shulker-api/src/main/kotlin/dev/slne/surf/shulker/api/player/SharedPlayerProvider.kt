package dev.slne.surf.shulker.api.player

import it.unimi.dsi.fastutil.objects.ObjectList

interface SharedPlayerProvider<P : ShulkerPlayer> {
    suspend fun findAll(): ObjectList<P>
    suspend fun findByName(name: String): P?
    suspend fun findByServiceName(serviceName: String): ObjectList<P>
    suspend fun playerCount(): Int
}