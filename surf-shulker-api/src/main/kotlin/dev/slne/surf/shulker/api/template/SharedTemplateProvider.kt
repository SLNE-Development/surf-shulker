package dev.slne.surf.shulker.api.template

import it.unimi.dsi.fastutil.objects.ObjectList

interface SharedTemplateProvider<T : Template> {
    suspend fun findAll(): ObjectList<T>

    suspend fun findByName(name: String): T?
}