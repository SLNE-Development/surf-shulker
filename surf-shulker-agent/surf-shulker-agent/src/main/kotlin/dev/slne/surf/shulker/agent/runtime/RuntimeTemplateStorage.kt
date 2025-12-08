package dev.slne.surf.shulker.agent.runtime

import dev.slne.surf.shulker.agent.service.AbstractService
import dev.slne.surf.shulker.api.template.SharedTemplateProvider
import dev.slne.surf.shulker.api.template.Template
import it.unimi.dsi.fastutil.objects.ObjectList

interface RuntimeTemplateStorage<T : Template, out S : AbstractService> : SharedTemplateProvider<T>,
    Reloadable {
    val templates: ObjectList<T>

    fun bindTemplate(service: @UnsafeVariance S)
    fun saveTemplate(template: T, service: @UnsafeVariance S)
    fun serviceTemplates(service: @UnsafeVariance S): ObjectList<T>
    fun create(name: String): T
    fun delete(template: T)
    fun update(template: T, newName: String)
}