package dev.slne.surf.shulker.runtime.common

import dev.slne.surf.shulker.api.service.Service
import dev.slne.surf.shulker.api.template.SharedTemplateProvider
import dev.slne.surf.shulker.api.template.Template
import dev.slne.surf.shulker.api.utils.Reloadable
import it.unimi.dsi.fastutil.objects.ObjectList

interface RuntimeTemplateStorage<T : Template, out S : Service> : SharedTemplateProvider<T>,
    Reloadable {
    val templates: ObjectList<Template>

    fun bindTemplate(service: @UnsafeVariance S)
    fun saveTemplate(template: T, service: @UnsafeVariance S)
    fun serviceTemplates(service: @UnsafeVariance S): ObjectList<T>
    fun create(name: String): T
    fun delete(template: T)
    fun update(template: T, newName: String)
}