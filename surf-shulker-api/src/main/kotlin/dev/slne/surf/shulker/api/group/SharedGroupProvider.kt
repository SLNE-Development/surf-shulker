package dev.slne.surf.shulker.api.group

interface SharedGroupProvider<G : Group> {
    suspend fun findAll(): List<G>
    suspend fun findByName(name: String): G?
    suspend fun create(group: G): G?
    suspend fun update(group: G): G?
    suspend fun delete(group: G): G?
}