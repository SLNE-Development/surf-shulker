package dev.slne.surf.shulker.runtime.common

import dev.slne.surf.shulker.runtime.common.bridge.Bridge
import dev.slne.surf.shulker.runtime.common.metadata.MetadataReader
import dev.slne.surf.shulker.runtime.common.metadata.MetadataTranslator
import dev.slne.surf.surfapi.core.api.util.freeze
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf

object PlatformPool {
    private val _platformPool = mutableObjectListOf<Platform>()
    private val _platformBridges = mutableObjectListOf<Bridge>()

    val platformPool get() = _platformPool.freeze()
    val platformBridges get() = _platformBridges.freeze()

    init {
        MetadataTranslator.read()
        MetadataReader.combineData()
    }

    fun findByName(name: String) = _platformPool.firstOrNull { it.name == name }
    fun findByBridgeId(bridgeId: String) = _platformBridges.firstOrNull { it.id == bridgeId }

    fun attach(platform: Platform) {
        if (findByName((platform.name)) != null) {
            throw IllegalArgumentException("Platform with name '${platform.name}' is already registered!")
        }

        _platformPool.add(platform)
    }

    val versionSize get() = _platformPool.sumOf { it.versions.size }
}