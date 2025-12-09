package dev.slne.surf.shulker.api.service

import dev.slne.surf.shulker.api.template.Template
import kotlinx.serialization.Serializable

fun sharedBootConfig(
    block: SharedBootConfig.() -> Unit
) = SharedBootConfig().apply(block)

@Serializable
class SharedBootConfig {
    var minMemory: Int? = null
    var maxMemory: Int? = null
    var templates: List<Template> = mutableListOf()
    var excludedTemplates: List<Template> = mutableListOf()
    var properties: Map<String, String> = mutableMapOf()

    companion object {
        val EMPTY = SharedBootConfig()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SharedBootConfig

        if (minMemory != other.minMemory) return false
        if (maxMemory != other.maxMemory) return false
        if (templates != other.templates) return false
        if (excludedTemplates != other.excludedTemplates) return false
        if (properties != other.properties) return false

        return true
    }

    override fun hashCode(): Int {
        var result = minMemory ?: 0
        result = 31 * result + (maxMemory ?: 0)
        result = 31 * result + templates.hashCode()
        result = 31 * result + excludedTemplates.hashCode()
        result = 31 * result + properties.hashCode()
        return result
    }

    override fun toString(): String {
        return "SharedBootConfig(minMemory=$minMemory, maxMemory=$maxMemory, templates=$templates, excludedTemplates=$excludedTemplates, properties=$properties)"
    }
}