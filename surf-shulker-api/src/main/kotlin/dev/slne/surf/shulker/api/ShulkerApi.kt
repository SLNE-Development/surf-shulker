package dev.slne.surf.shulker.api

import dev.slne.surf.shulker.api.event.SharedEventProvider
import dev.slne.surf.shulker.api.group.SharedGroupProvider
import dev.slne.surf.shulker.api.information.SharedCloudInformationProvider
import dev.slne.surf.shulker.api.platform.SharedPlatformProvider
import dev.slne.surf.shulker.api.player.SharedPlayerProvider
import dev.slne.surf.shulker.api.service.SharedServiceProvider
import dev.slne.surf.shulker.api.template.SharedTemplateProvider
import dev.slne.surf.surfapi.core.api.util.requiredService

private val shulkerApi = requiredService<ShulkerApi>()

interface ShulkerApi {
    val eventProvider: SharedEventProvider
    val serviceProvider: SharedServiceProvider<*>
    val groupProvider: SharedGroupProvider<*>
    val playerProvider: SharedPlayerProvider<*>
    val cloudInformationProvider: SharedCloudInformationProvider<*>
    val platformProvider: SharedPlatformProvider<*>
    val templateProvider: SharedTemplateProvider<*>

    companion object : ShulkerApi by shulkerApi {
        val INSTANCE get() = shulkerApi
    }
}