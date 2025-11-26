package dev.slne.surf.shulker.node.docker.utils

import com.github.dockerjava.api.DockerClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun DockerClient.pullImage(imageName: String) = withContext(Dispatchers.IO) {
    pullImageCmd(imageName).start().awaitCompletion()
}