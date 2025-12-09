package dev.slne.surf.shulker.api.utils.os

enum class OS(
    val nativeExecutableSuffix: String?,
    val shellPrefix: Array<String>,
    val currentDirectoryPrefix: String?
) {
    WINDOWS(".exe", arrayOf("cmd", "/c"), null),
    LINUX(null, arrayOf("sh", "-c"), "./"),
    MACOS(null, arrayOf("sh", "-c"), "./"),
    UNKNOWN(null, emptyArray(), null);

    fun executableCurrentDirectoryCommand(fileName: String) =
        arrayOf(*shellPrefix, "${currentDirectoryPrefix ?: ""}$fileName")
}

val currentOs: OS by lazy {
    val osName = System.getProperty("os.name").lowercase()

    when {
        osName.contains("win") -> OS.WINDOWS
        osName.contains("mac") -> OS.MACOS
        osName.contains("nix") || osName.contains("nux") || osName.contains("aix") -> OS.LINUX
        else -> OS.UNKNOWN
    }
}

val currentCpuArchitecture: String by lazy {
    System.getProperty("os.arch")
}