@file:OptIn(ExperimentalPathApi::class)

package dev.slne.surf.shulker.runtime.common.task.action.actions

import dev.slne.surf.shulker.runtime.common.PlatformParameters
import dev.slne.surf.shulker.runtime.common.task.PlatformTaskStep
import dev.slne.surf.shulker.runtime.common.task.action.PlatformAction
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively

class PlatformDirectoryDeleteAction : PlatformAction() {
    override suspend fun run(
        file: Path,
        step: PlatformTaskStep,
        environment: PlatformParameters
    ) {
        file.deleteRecursively()
    }
}