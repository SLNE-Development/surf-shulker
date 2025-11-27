package dev.slne.surf.shulker.spring.utils.exceptions

import org.apache.commons.lang3.StringUtils
import org.apache.commons.text.WordUtils
import org.springframework.boot.ExitCodeGenerator

private const val LINE_WIDTH = 80

@DslMarker
annotation class FatalShulkerErrorDsl

fun fatalShulkerError(builder: FatalShulkerError.Builder.() -> Unit) =
    FatalShulkerError.builder().apply(builder).build()

class FatalShulkerError private constructor(
    private val simpleErrorMessage: String?,
    private val detailedErrorMessage: String?,
    override val cause: Throwable?,
    private val additionalInformation: List<String>,
    private val possibleSolutions: List<String>,
    private val exitCode: Int
) : Error(), ExitCodeGenerator {
    fun buildMessage() = buildString {
        append("╔")
        append(StringUtils.repeat("═", LINE_WIDTH))
        append("╗")
        appendLine()
        appendIndentedLine("Fatal Error Occurred: ", simpleErrorMessage)
        appendIndentedLine("Detailed Error Message: ", detailedErrorMessage)
        appendIndentedLine("Cause: ", cause?.message)

        if (additionalInformation.isNotEmpty()) {
            appendClosedLine("Additional Information: ")
            for (info in additionalInformation) {
                appendIndentedLine(" - ", info)
            }
        }

        if (possibleSolutions.isNotEmpty()) {
            appendClosedLine("Possible Solutions: ")
            for (solution in possibleSolutions) {
                appendIndentedLine(" - ", solution)
            }
        }

        append("╚")
        append(StringUtils.repeat("═", LINE_WIDTH))
        append("╝")
    }

    private fun StringBuilder.appendClosedLine(line: String) {
        append("║ ")
        append(line)
        append(StringUtils.repeat(" ", LINE_WIDTH - line.length - 1)) // Padding for border
        append("║")
        appendLine()
    }

    private fun StringBuilder.appendIndentedLine(
        indentPrefix: String,
        line: String?
    ) {
        line ?: return

        val indentLength = indentPrefix.length
        val wrappedLines = WordUtils.wrap(line, LINE_WIDTH - indentLength - 3).lineSequence()

        appendClosedLine(indentPrefix + wrappedLines.first())

        for (wrappedLine in wrappedLines.drop(1)) {
            appendClosedLine(StringUtils.repeat(" ", indentLength) + wrappedLine)
        }
    }

    override fun getExitCode(): Int = exitCode

    companion object {
        fun builder() = Builder()
    }

    @FatalShulkerErrorDsl
    class Builder {
        private var simpleErrorMessage: String? = null
        private var detailedErrorMessage: String? = null
        private var cause: Throwable? = null
        private val additionalInformation = mutableListOf<String>()
        private val possibleSolutions = mutableListOf<String>()
        private var exitCode: Int = 0

        fun simpleErrorMessage(message: String) = apply {
            this.simpleErrorMessage = message
        }

        fun detailedErrorMessage(message: String) = apply {
            this.detailedErrorMessage = message
        }

        fun cause(throwable: Throwable) = apply {
            this.cause = throwable
        }

        fun additionalInformation(info: List<String>) = apply {
            this.additionalInformation.addAll(info)
        }

        fun possibleSolutions(solutions: List<String>) = apply {
            this.possibleSolutions.addAll(solutions)
        }

        fun addAdditionalInformation(info: String) = apply {
            this.additionalInformation.add(info)
        }

        fun addPossibleSolution(solution: String) = apply {
            this.possibleSolutions.add(solution)
        }

        fun exitCode(code: Int) = apply {
            this.exitCode = code
        }

        fun build() = FatalShulkerError(
            simpleErrorMessage,
            detailedErrorMessage,
            cause,
            additionalInformation,
            possibleSolutions,
            exitCode
        )
    }
}