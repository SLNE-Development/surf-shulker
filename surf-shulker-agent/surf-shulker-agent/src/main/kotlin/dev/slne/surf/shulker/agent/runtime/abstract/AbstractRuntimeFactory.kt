package dev.slne.surf.shulker.agent.runtime.abstract

import dev.slne.surf.shulker.agent.Agent
import dev.slne.surf.shulker.agent.runtime.RuntimeFactory
import dev.slne.surf.shulker.agent.service.AbstractService
import dev.slne.surf.shulker.agent.utils.JavaUtils
import dev.slne.surf.shulker.api.service.events.ServiceChangeStateEvent
import dev.slne.surf.shulker.proto.service.ServiceSnapshot
import dev.slne.surf.shulker.proto.service.ServiceState
import dev.slne.surf.shulker.runtime.common.Platform
import dev.slne.surf.shulker.runtime.common.PlatformParameters
import dev.slne.surf.shulker.runtime.common.ServerPlatformForwarding
import dev.slne.surf.surfapi.core.api.util.logger
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.synchronize
import dev.slne.surf.surfapi.core.api.util.toObjectList
import it.unimi.dsi.fastutil.objects.ObjectList
import kotlinx.coroutines.runBlocking
import kotlinx.io.files.Path
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class AbstractRuntimeFactory<S : AbstractService>(
    val factoryPath: Path
) : RuntimeFactory<S> {
    private val log = logger()
    private val eventProvider get() = Agent.eventProvider

    val cacheThreadPool: ExecutorService by lazy {
        Executors.newFixedThreadPool(Agent.config.maxCachingProcesses)
    }
    val runningCacheProcesses: ObjectList<Pair<String, String>> by lazy {
        mutableObjectListOf<Pair<String, String>>().synchronize()
    }
    val waitingServices: ObjectList<S> by lazy {
        mutableObjectListOf<S>().synchronize()
    }

    override suspend fun bootApplication(service: S) {
        if (service.state != ServiceState.PREPARING) {
            log.atSevere()
                .log("Service ${service.name} is not in PREPARING state, cannot boot application.")
            return
        }

        val platform = service.group.platform
        val version = service.group.platformIndex.version

        val environment = this.environment(service)

        val path = service.path
        path.createDirectories()

        val cacheIsRunning =
            runningCacheProcesses.any { platform.name == it.first && version == it.second }

        if (!platform.cacheExists(version) || cacheIsRunning) {
            waitingServices.add(service)

            if (!cacheIsRunning) {
                this.handleMissingCache(platform, version, environment)
            }

            return
        }

        log.atInfo().log("Booting application for service ${service.name}...")
        service.state = ServiceState.STARTING

        eventProvider.call(
            ServiceChangeStateEvent(
                service,
                ServiceState.PREPARING,
                ServiceState.STARTING
            )
        )

        Agent.runtime.templateStorage.bindTemplate(service)
        platform.prepare(path, service.group.platform.version, environment)

        this.runRuntimeBoot(service)
    }

    override suspend fun shutdownApplication(
        service: S,
        shutdownCleanup: Boolean
    ): ServiceSnapshot {
        if (service.state == ServiceState.STOPPING || service.state == ServiceState.OFFLINE) {
            log.atWarning().log("Service ${service.name} is already stopping or stopped.")
            return service.toSnapshot()
        }

        service.state = ServiceState.STOPPING
        eventProvider.call(
            ServiceChangeStateEvent(
                service,
                service.state,
                ServiceState.STOPPING
            )
        )

        log.atInfo().log("Shutting down application for service ${service.name}...")
        eventProvider.dropServiceSubscriptions(service)

        this.runRuntimeShutdown(service, shutdownCleanup)

        service.state = ServiceState.OFFLINE
        eventProvider.call(
            ServiceChangeStateEvent(
                service,
                ServiceState.STOPPING,
                ServiceState.OFFLINE
            )
        )

        Agent.runtime.serviceStorage.dropAbstractService(service)
        log.atInfo().log("Service ${service.name} has been shut down.")

        return service.toSnapshot()
    }

    abstract suspend fun runRuntimeBoot(service: S)
    abstract suspend fun runRuntimeShutdown(service: S, shutdownCleanup: Boolean)

    protected suspend fun environment(service: S): PlatformParameters {
        val version = service.group.platform.version
        val platform = service.group.platform
        val versionObject = platform.version(version)

        val environment = PlatformParameters(versionObject)

        environment["hostname"] = service.hostname
        environment["port"] = service.port.toString()
        environment["agent-port"] = Agent.config.port
        environment["agent-hostname"] = Agent.runtime.detectedLocalAddress
        environment["service-name"] = service.name
        environment["velocity-proxy-token"] = Agent.securityProvider.proxySecurityToken
        environment["file-name"] = service.group.applicationPlatformFile.name

        val velocityPlatforms = listOf("velocity", "gate")
        val groupStorage = Agent.runtime.groupStorage

        val groups = groupStorage.findAll()

        val modernForwardingMode = groups.filter { it.isServer }
            .all { it.platform.forwarding == ServerPlatformForwarding.MODERN }

        environment["forwarding-mode"] =
            (if (modernForwardingMode) ServerPlatformForwarding.MODERN.name else ServerPlatformForwarding.LEGACY.name).lowercase()

        environment["velocity-use"] =
            groups.stream().anyMatch { velocityPlatforms.contains(it.platform.name) }
        environment["bungeecord-use"] = groups.stream()
            .anyMatch { it.platform.name == "bungeecord" }
        environment["version"] = shulkerVersion

        return environment
    }

    protected fun handleMissingCache(
        platform: Platform,
        version: String,
        environment: PlatformParameters
    ) {
        val platformName = platform.name

        val processEntry = Pair(platformName, version)
        runningCacheProcesses.add(processEntry)

        cacheThreadPool.execute {
            runBlocking {
                platform.cachePrepare(version, environment)
                runningCacheProcesses.remove(processEntry)

                val servicesToBoot = waitingServices.filter {
                    val group = it.group

                    group.platform.name == platform.name && group.platform.version == version
                }

                servicesToBoot.forEach {
                    bootApplication(it)
                }

                waitingServices.removeAll(servicesToBoot)
            }
        }
    }

    protected fun languageSpecificBootArguments(service: S): ObjectList<String> {
        val commands = mutableObjectListOf<String>()

        commands.addAll(javaLanguagePath(service))
        commands.addAll(
            listOf(
                "-Dterminal.jline=false",
                "-Dfile.encoding=UTF-8",
                "-Xms${service.minMemory}M",
                "-Xmx${service.maxMemory}M"
            )
        )

        commands.addAll(service.platform.flags)
        commands.addAll(
            listOf(
                "-jar",
                service.group.applicationPlatformFile.name
            )
        )
        commands.addAll(service.platform.arguments)

        return commands
    }

    protected fun javaLanguagePath(service: S): ObjectList<String> {
        val javaPath = service.group.properties.get(JAVA_PATH)?.takeIf {
            JavaUtils.isValidJavaPath(it)
        } ?: System.getProperty("java.home")

        return listOf("$javaPath/bin/java").toObjectList()
    }
}