package dev.slne.surf.shulker.api

import dev.slne.surf.shulker.api.event.SharedEventProvider
import dev.slne.surf.shulker.api.group.Group
import dev.slne.surf.shulker.api.group.SharedGroupProvider
import dev.slne.surf.shulker.api.information.CloudInformation
import dev.slne.surf.shulker.api.information.SharedCloudInformationProvider
import dev.slne.surf.shulker.api.platform.Platform
import dev.slne.surf.shulker.api.platform.SharedPlatformProvider
import dev.slne.surf.shulker.api.player.SharedPlayerProvider
import dev.slne.surf.shulker.api.player.ShulkerPlayer
import dev.slne.surf.shulker.api.service.Service
import dev.slne.surf.shulker.api.service.SharedServiceProvider
import dev.slne.surf.shulker.api.template.SharedTemplateProvider
import dev.slne.surf.shulker.api.template.Template
import dev.slne.surf.shulker.runtime.common.Runtime
import dev.slne.surf.shulker.runtime.common.RuntimeExpender
import dev.slne.surf.shulker.runtime.common.RuntimeFactory
import dev.slne.surf.surfapi.core.api.util.requiredService

private val shulkerApi = requiredService<ShulkerApi>()

interface ShulkerApi {
    val runtime: Runtime
    val factory: RuntimeFactory<*> get() = runtime.factory
    val expender: RuntimeExpender<*> get() = runtime.expender
    
    val eventProvider: SharedEventProvider
    val serviceProvider: SharedServiceProvider<out Service>
    val groupProvider: SharedGroupProvider<out Group>
    val playerProvider: SharedPlayerProvider<out ShulkerPlayer>
    val cloudInformationProvider: SharedCloudInformationProvider<out CloudInformation>
    val platformProvider: SharedPlatformProvider<out Platform>
    val templateProvider: SharedTemplateProvider<out Template>

    companion object : ShulkerApi by shulkerApi {
        val INSTANCE get() = shulkerApi
    }
}