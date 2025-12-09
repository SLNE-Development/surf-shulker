package dev.slne.surf.shulker.runtime.docker

import com.github.dockerjava.api.DockerClient
import dev.slne.surf.shulker.api.template.Template

class DockerImage(
    val client: DockerClient,
    name: String
) : Template(name) {
    override val size: String
        get() {
            val info = client.inspectImageCmd(name).exec()
            val sizeBytes = info.size ?: 0L

            return humanReadableSize(sizeBytes)
        }

}
