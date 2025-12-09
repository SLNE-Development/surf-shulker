package dev.slne.surf.shulker.runtime.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.model.Frame
import dev.slne.surf.shulker.agent.runtime.RuntimeExpender
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectList

class DockerExpender(val client: DockerClient) : RuntimeExpender<DockerService> {
    override suspend fun executeCommand(service: DockerService, command: String): Boolean {
        val output = mutableObjectListOf<String>()
        val execId =
            client.execCreateCmd(service.containerId!!)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withCmd("sh", "-c", command)
                .exec().id

        client.execStartCmd(execId)
            .exec(object : ResultCallback.Adapter<Frame>() {
                override fun onNext(frame: Frame?) {
                    frame?.payload?.let { output.add(String(it).trim()) }
                }
            }).awaitCompletion()

        val inspect = client.inspectExecCmd(execId).exec()

        return inspect.exitCodeLong == 0L
    }

    override suspend fun readLogs(service: DockerService, lines: Int): ObjectList<String> {
        val logLines = mutableObjectListOf<String>()

        client.logContainerCmd(service.containerId!!)
            .withTail(lines)
            .withStdOut(true)
            .withStdErr(true)
            .exec(object : ResultCallback.Adapter<Frame>() {
                override fun onNext(frame: Frame?) {
                    frame?.payload?.let { logLines.add(String(it).trim()) }
                }
            }).awaitCompletion()

        return logLines
    }
}