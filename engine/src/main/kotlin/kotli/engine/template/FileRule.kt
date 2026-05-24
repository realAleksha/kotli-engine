package kotli.engine.template

import kotli.engine.extensions.takeIfIndex
import org.slf4j.LoggerFactory

/**
 * Rule to be applied to a template #file during the generation of the output structure.
 */
abstract class FileRule {

    protected val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Applies this rule to the given template #file.
     *
     * @param file The template file to apply the rule to.
     */
    fun apply(file: TemplateFile) {
        logger.debug("apply rule :: {} -> {}", this, file.path)
        doApply(file)
    }

    protected fun isMarked(file: TemplateFile, line: String, marker: String, ignoreCase: Boolean = true): Boolean {
        return line.contains(marker, ignoreCase)
    }

    protected fun cleanup(file: TemplateFile, line: String): String {
        val startIndex = line.indexOfAny(file.markerSeparators).takeIfIndex() ?: return line
        val newLine = line.substring(0, startIndex).trimEnd()
        return newLine
    }

    abstract fun doApply(file: TemplateFile)

}