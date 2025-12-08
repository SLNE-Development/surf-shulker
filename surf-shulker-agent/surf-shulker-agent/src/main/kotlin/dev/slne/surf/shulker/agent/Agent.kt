package dev.slne.surf.shulker.agent

import dev.slne.surf.shulker.agent.runtime.Runtime
import dev.slne.surf.shulker.api.ShulkerApi
import dev.slne.surf.shulker.api.event.SharedEventProvider
import dev.slne.surf.shulker.api.group.SharedGroupProvider
import dev.slne.surf.shulker.api.information.SharedCloudInformationProvider
import dev.slne.surf.shulker.api.platform.SharedPlatformProvider
import dev.slne.surf.shulker.api.player.SharedPlayerProvider
import dev.slne.surf.shulker.api.service.SharedServiceProvider
import dev.slne.surf.shulker.api.template.SharedTemplateProvider
import dev.slne.surf.surfapi.core.api.util.logger


object Agent : ShulkerApi {
    private val log = logger()

    val runtime = Runtime.create()

    override val eventProvider: SharedEventProvider
    override val serviceProvider: SharedServiceProvider<*>
    override val groupProvider: SharedGroupProvider<*>
    override val playerProvider: SharedPlayerProvider<*>
    override val cloudInformationProvider: SharedCloudInformationProvider<*>
    override val platformProvider: SharedPlatformProvider<*>
    override val templateProvider: SharedTemplateProvider<*>

    fun init() {
        log.atInfo().log(
        checkForUpdates()

        runtime.init()
    }
}