package kotli.engine.template.rule

import kotli.engine.template.FileRule
import kotli.engine.template.TemplateFile

/**
 * Cleans up all lines containing a given marker by removing the marker
 * and possible cleanup prefix (which can be set in the constructor).
 *
 * @param marker The text that must be present in a line to consider it for modification.
 * @param singleLine If true, only the first found line will be processed.
 */
data class CleanupMarkedLine(
    private val marker: String,
    private val singleLine: Boolean = false,
    private val ignoreCase: Boolean = true
) : FileRule() {

    override fun doApply(file: TemplateFile) {
        val lines = file.lines
        lines.forEachIndexed { index, line ->
            if (isMarked(file, line, marker, ignoreCase)) {
                lines[index] = cleanup(file, line)
                if (singleLine) return
            }
        }
    }

}