package dev.slne.surf.shulker.runtime.common.bridge

import dev.slne.surf.shulker.api.utils.config.readYml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarFile
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.io.path.Path
import kotlin.io.path.exists

const val groupId = "dev.slne.surf.shulker"
const val repoUrl = "https://repo.slne.dev/maven-public"

data class Bridge(
    val id: String,
    val version: String
) {
    var type: BridgeType? = null
        get() {
            if (field == null) {
                updateContext()
            }

            return field
        }
        private set

    var bridgeClass: String? = null
        get() {
            if (field == null) {
                updateContext()
            }

            return field
        }
        private set

    val path: Path get() = Path("local/libs/$id-$version.jar")
    val isDownloaded get() = path.exists()

    suspend fun download() = this.downloadLatestSnapshotJar(groupId, id, version, repoUrl, path)

    fun updateContext() {
        JarFile(path.toFile()).use { jar ->
            val entry = jar.getEntry("bridge.json") ?: return

            jar.getInputStream(entry).use { input ->
                val yml = input.readYml<BridgeConfig>()

                this.type = yml.type
                this.bridgeClass = yml.className
            }
        }
    }

    suspend fun downloadLatestSnapshotJar(
        groupId: String,
        artifactId: String,
        version: String,
        repoUrl: String,
        target: Path
    ): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val groupPath = groupId.replace('.', '/')
            val base = "$repoUrl/$groupPath/$artifactId/$version"

            val metadataUrl = URI("$base/maven-metadata.xml").toURL()
            val xml = metadataUrl.openStream()

            val doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(xml)

            val snapshot = doc.getElementsByTagName("snapshot").item(0)
            val timestamp = snapshot.childNodes.item(1).textContent
            val buildNumber = snapshot.childNodes.item(3).textContent

            val baseVersion = version.removeSuffix("-SNAPSHOT")
            val snapshotVersion = "$baseVersion-$timestamp-$buildNumber"
            val jarName = "$artifactId-$snapshotVersion.jar"

            val jarUrl = URI("$base/$jarName").toURL()

            jarUrl.openStream().use { inputStream ->
                Files.copy(inputStream, target)
            }

            true
        }.getOrElse {
            it.printStackTrace()
            false
        }
    }
}