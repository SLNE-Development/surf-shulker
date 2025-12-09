package dev.slne.surf.shulker.runtime.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.model.Event
import com.github.dockerjava.api.model.EventType
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.okhttp.OkDockerHttpClient
import dev.slne.surf.shulker.api.utils.network.localAddress
import dev.slne.surf.shulker.proto.service.ServiceState
import kotlinx.coroutines.launch
import kotlin.jvm.optionals.getOrElse

class DockerRuntime : Runtime() {
    private val client: DockerClient = createLocalDockerClient()
    override val serviceStorage = DockerRuntimeServiceStorage(client)
    override val groupStorage = DockerRuntimeGroupStorage
    override val expender = DockerExpender(client)
    override val serviceStatsThread = DockerServiceStatsJob(client)
    override val factory = DockerRuntimeFactory(client)
    override val templateStorage = DockerTemplateStorage(client)
    override val configHolder = DockerConfigHolder
    private val informationJob = DockerCloudInformationJob
    private val queue = DockerThreadedRuntimeQueue

    override val detectedLocalAddress: String
        get() = localAddress()

    override fun sendCommand(command: String) {
        TODO("Not yet implemented")
    }

    private fun createLocalDockerClient(): DockerClient {
        val config = DefaultDockerClientConfig.createDefaultConfigBuilder().build()
        val httpClient = OkDockerHttpClient.Builder()
            .dockerHost(config.dockerHost)
            .sslConfig(config.sslConfig)
            .build()

        return DockerClientImpl.getInstance(config, httpClient)
    }

    override fun boot() {
        listenForContainerEvents()
        informationJob.start()
        queue.start()
    }

    private fun listenForContainerEvents() {
        client.eventsCmd().exec(object : ResultCallback.Adapter<Event>() {
            override fun onNext(event: Event?) {
                if (event == null) return

                try {
                    if (event.type == EventType.CONTAINER) {
                        runtimeScope.launch {
                            val service = serviceStorage.findAll().stream()
                                .filter { it.containerId == event.id }.findFirst()
                                .getOrElse { return@launch }

                            if (event.action == "destroy") {
                                service.changeState(ServiceState.STOPPING)
                            } else if (event.action == "die") {
                                factory.shutdownApplication(service, true)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

}