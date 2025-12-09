package dev.slne.surf.shulker.api.utils.filesystem

import kotlinx.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

fun copyDirectory(sourcePath: Path, targetPath: Path) {
    Files.walkFileTree(sourcePath, object : SimpleFileVisitor<Path>() {
        override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
            val targetDir = targetPath.resolve(sourcePath.relativize(dir))

            if (!Files.exists(targetDir)) {
                Files.createDirectory(targetDir)
            }

            return FileVisitResult.CONTINUE
        }

        override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
            try {
                Files.copy(
                    file,
                    targetPath.resolve(sourcePath.relativize(file)),
                    StandardCopyOption.REPLACE_EXISTING
                )
            } catch (e: Exception) {
                System.err.println("Cannot copy file: ${file.toAbsolutePath()}")
                e.printStackTrace()
            }

            return FileVisitResult.CONTINUE
        }
    })
}

fun deleteDirectory(path: Path) {
    if (!Files.exists(path)) {
        return
    }

    try {
        Files.walkFileTree(path, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                Files.delete(file)
                return FileVisitResult.CONTINUE
            }

            override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
                Files.delete(dir)
                return FileVisitResult.CONTINUE
            }
        })
    } catch (e: Exception) {
        System.err.println("Cannot delete directory: ${path.toAbsolutePath()}")
        e.printStackTrace()
    }
}