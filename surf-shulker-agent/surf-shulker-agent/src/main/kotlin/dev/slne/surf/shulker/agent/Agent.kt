package dev.slne.surf.shulker.agent

import dev.slne.surf.shulker.agent.event.AgentEventProvider
import dev.slne.surf.shulker.agent.information.CloudInformationStorageImpl
import dev.slne.surf.shulker.agent.module.ModuleProvider
import dev.slne.surf.shulker.agent.platform.PlatformStorageImpl
import dev.slne.surf.shulker.agent.player.PlayerStorageImpl
import dev.slne.surf.shulker.runtime.common.Runtime
import dev.slne.surf.shulker.agent.security.SecurityProvider
import dev.slne.surf.shulker.api.ShulkerApi
import dev.slne.surf.shulker.api.group.SharedGroupProvider
import dev.slne.surf.shulker.api.information.SharedCloudInformationProvider
import dev.slne.surf.shulker.api.platform.SharedPlatformProvider
import dev.slne.surf.shulker.api.service.SharedServiceProvider
import dev.slne.surf.shulker.api.template.SharedTemplateProvider
import dev.slne.surf.surfapi.core.api.util.logger


object Agent : ShulkerApi {
    private val log = logger()

    val runtime = Runtime.create()

    val moduleProvider = ModuleProvider
    val securityProvider = SecurityProvider
    val informationStorage = CloudInformationStorageImpl
    val platformStorage = PlatformStorageImpl

    override val playerProvider = PlayerStorageImpl
    override val eventProvider = AgentEventProvider
    override val serviceProvider: SharedServiceProvider<*> = runtime.serviceStorage
    override val groupProvider: SharedGroupProvider<*> = runtime.groupStorage
    override val cloudInformationProvider: SharedCloudInformationProvider<*> = informationStorage
    override val platformProvider: SharedPlatformProvider<*> = platformStorage
    override val templateProvider: SharedTemplateProvider<*> = runtime.templateStorage

    fun init() {
        log.atInfo().log(
        checkForUpdates()

        runtime.init()
    }
}