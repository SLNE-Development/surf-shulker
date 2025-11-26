package dev.slne.surf.shulker.node.docker.utils

import com.github.dockerjava.api.model.Container
import dev.slne.surf.shulker.node.docker.container.DockerContainer
import java.util.*

private const val PREFIX = "surf.shulker.node.container"

enum class SurfLabel(
    val label: String,
    val valueBuilder: (String) -> DockerLabel<*>
) {
    UUID_LABEL(
        "$PREFIX.uuid",
        { DockerLabel.UuidDockerLabel(UUID.fromString(it)) }
    ),
    PORT_LABEL(
        "$PREFIX.port",
        { DockerLabel.IntDockerLabel(it.toInt()) }
    ),
    PERSISTENT_VOLUMES_LABEL(
        "$PREFIX.persistentVolumes",
        { DockerLabel.BooleanDockerLabel(it.toBoolean()) }
    );
    
    companion object {
        fun fromDockerContainer(container: Container): Map<SurfLabel, DockerLabel<*>> {
            val labels = container.labels
            val result = mutableMapOf<SurfLabel, DockerLabel<*>>()

            for (surfLabel in entries) {
                val labelValue = labels[surfLabel.label]

                if (labelValue != null) {
                    result[surfLabel] = surfLabel.valueBuilder(labelValue)
                }
            }

            return result
        }

        fun toMap(container: DockerContainer): Map<String, String> {
            return mapOf(
                UUID_LABEL.label to container.uuid.toString(),
                PORT_LABEL.label to container.port.toString(),
                PERSISTENT_VOLUMES_LABEL.label to container.persistentVolumes.toString()
            )
        }
    }
}

sealed interface DockerLabel<V : Any> {
    data class StringDockerLabel(val value: String) : DockerLabel<String>
    data class IntDockerLabel(val value: Int) : DockerLabel<Int>
    data class BooleanDockerLabel(val value: Boolean) : DockerLabel<Boolean>
    data class UuidDockerLabel(val value: UUID) : DockerLabel<UUID>
}