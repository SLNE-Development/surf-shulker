package dev.slne.surf.shulker.api.template

import dev.slne.surf.shulker.proto.template.TemplateSnapshot
import dev.slne.surf.shulker.proto.template.templateSnapshot
import kotlin.math.log10
import kotlin.math.pow

open class Template(
    val name: String,
    private val size: String = "unknown"
) {
    fun toSnapshot() = templateSnapshot {
        this.name = this@Template.name
        this.size = this@Template.size
    }

    protected fun humanReadableSize(bytes: Long): String {
        if (bytes < 0) return "empty"

        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(bytes.toDouble() / log10(1024.0))).toInt()
        val humanValue = bytes / 1024.0.pow(digitGroups.toDouble())

        return "%.1f %s".format(humanValue, units[digitGroups])
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Template

        if (name != other.name) return false
        if (size != other.size) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + size.hashCode()
        return result
    }

    override fun toString(): String {
        return "Template(name='$name', size='$size')"
    }

    companion object {
        fun fromSnapshot(snapshot: TemplateSnapshot) = Template(
            name = snapshot.name,
            size = snapshot.size
        )

        fun fromSnapshotList(snapshots: Collection<TemplateSnapshot>) =
            snapshots.map { fromSnapshot(it) }
    }
}