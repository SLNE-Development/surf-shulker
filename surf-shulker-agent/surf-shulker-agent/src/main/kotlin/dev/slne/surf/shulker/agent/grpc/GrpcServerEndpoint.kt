package dev.slne.surf.shulker.agent.grpc

import dev.slne.surf.shulker.agent.event.EventGrpcService
import dev.slne.surf.shulker.agent.group.GroupGrpcService
import dev.slne.surf.shulker.agent.information.CloudInformationGrpcService
import dev.slne.surf.shulker.agent.platform.PlatformGrpcService
import dev.slne.surf.shulker.agent.player.PlayerGrpcService
import dev.slne.surf.shulker.agent.service.ServiceGrpcService
import dev.slne.surf.shulker.agent.template.TemplateGrpcService
import dev.slne.surf.shulker.grpc.server.ShulkerGrpcServer
import org.gradle.internal.impldep.org.apache.commons.compress.harmony.pack200.PackingUtils.config

object GrpcServerEndpoint : ShulkerGrpcServer(
    config,
    listOf(
        EventGrpcService,
        GroupGrpcService,
        ServiceGrpcService,
        PlayerGrpcService,
        CloudInformationGrpcService,
        PlatformGrpcService,
        TemplateGrpcService
    )
) {
}