package dev.slne.surf.shulker.agent.template

import dev.slne.surf.shulker.agent.Agent
import dev.slne.surf.shulker.proto.template.FindTemplateByNameRequest
import dev.slne.surf.shulker.proto.template.TemplateControllerGrpcKt
import dev.slne.surf.shulker.proto.template.TemplateResult
import dev.slne.surf.shulker.proto.template.templateResult

object TemplateGrpcService : TemplateControllerGrpcKt.TemplateControllerCoroutineImplBase() {
    private val templateStorage get() = Agent.runtime.templateStorage

    override suspend fun findByName(request: FindTemplateByNameRequest): TemplateResult {
        val name = request.name

        return templateResult {
            this.templates.addAll(
                if (name.isNotEmpty()) {
                templateStorage.findByName(name)?.let { listOf(it.toSnapshot()) } ?: emptyList()
            } else {
                templateStorage.findAll().map { it.toSnapshot() }
            })
        }
    }
}