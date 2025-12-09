package dev.slne.surf.shulker.runtime.docker

import dev.slne.surf.shulker.agent.runtime.abstract.AbstractGroupStorage
import kotlin.io.path.Path

object DockerRuntimeGroupStorage : AbstractGroupStorage(Path("local/groups"))