package dev.slne.surf.shulker.runtime.common

import com.google.gson.GsonBuilder
import dev.slne.surf.shulker.common.json.RuntimeTypeAdapterFactory
import dev.slne.surf.shulker.runtime.common.task.action.PlatformAction
import dev.slne.surf.shulker.runtime.common.task.action.actions.*
import kotlin.io.path.Path

val PLATFORM_PATH = Path("local/metadata")
const val PLATFORM_METADATA_URL =
    "https://raw.githubusercontent.com/SLNE-Development/surf-shulker-metadata/refs/head/master/"

val PLATFORM_GSON = GsonBuilder()
    .setPrettyPrinting()
    .serializeNulls()
    .registerTypeHierarchyAdapter(PlatformVersion::class.java, PlatformVersionSerializer)
    .registerTypeAdapter(Platform::class.java, PlatformDeserializer)
    .registerTypeAdapterFactory(
        RuntimeTypeAdapterFactory
            .of<PlatformAction>()
            .registerSubtype<PlatformFileReplacementAction>()
            .registerSubtype<PlatformFileWriteAction>()
            .registerSubtype<PlatformFilePropertyUpdateAction>()
            .registerSubtype<PlatformFileUnzipAction>()
            .registerSubtype<PlatformFileDeleteAction>()
            .registerSubtype<PlatformExecuteCommandAction>()
            .registerSubtype<PlatformDirectoryDeleteAction>()
            .registerSubtype<PlatformFileMoveAction>()
            .registerSubtype<PlatformDownloadAction>()
    )
    .create()