package dev.slne.surf.shulker.agent.information

import dev.slne.surf.shulker.api.information.CloudInformation
import dev.slne.surf.shulker.api.information.SharedCloudInformationProvider

interface CloudInformationStorage : SharedCloudInformationProvider<CloudInformation> {
    fun addCloudInformation(cloudInformation: CloudInformation)
    fun removeCloudInformation(cloudInformation: CloudInformation)
    fun saveCurrentCloudInformation()
}