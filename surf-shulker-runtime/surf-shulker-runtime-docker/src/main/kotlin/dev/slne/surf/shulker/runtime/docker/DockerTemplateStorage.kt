package dev.slne.surf.shulker.runtime.docker

import com.github.dockerjava.api.DockerClient
import dev.slne.surf.shulker.agent.runtime.RuntimeTemplateStorage
import dev.slne.surf.shulker.api.template.Template
import dev.slne.surf.surfapi.core.api.util.toObjectList
import it.unimi.dsi.fastutil.objects.ObjectList
import kotlin.collections.map
import kotlin.text.removePrefix
import kotlin.text.startsWith

class DockerTemplateStorage(
    val client: DockerClient
) : RuntimeTemplateStorage<DockerImage, DockerService> {
    override val templates: ObjectList<Template>
        get() = client.listImagesCmd().exec()
            .filter { img -> img.labels?.get(LABEL_MANAGED) == "true" }
            .flatMap { it.repoTags?.toList() ?: emptyList() }
            .filter { it.startsWith(PREFIX) }
            .map { Template(it.removePrefix(PREFIX)) }
            .toObjectList()

    override fun bindTemplate(service: DockerService) {
        TODO("Not yet implemented")
    }

    override fun saveTemplate(template: DockerImage, service: DockerService) {
        TODO("Not yet implemented")
    }

    override fun serviceTemplates(service: DockerService): ObjectList<DockerImage> {
        TODO("Not yet implemented")
    }

    override fun create(name: String): DockerImage {
        TODO("Not yet implemented")
    }

    override fun delete(template: DockerImage) {
        TODO("Not yet implemented")
    }

    override fun update(template: DockerImage, newName: String) {
        TODO("Not yet implemented")
    }

    override suspend fun findAll(): ObjectList<DockerImage> {
        return templates.map { DockerImage(client, it.name) }.toObjectList()
    }

    override suspend fun findByName(name: String): DockerImage? {
        TODO("Not yet implemented")
    }

    override suspend fun reload() {
        TODO("Not yet implemented")
    }

    companion object {
        private const val PREFIX = "shulker/"
        private const val LABEL_MANAGED = "shulker.managed"
    }
}