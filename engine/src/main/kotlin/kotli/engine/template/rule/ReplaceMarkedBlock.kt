package kotli.engine.template.rule

import kotli.engine.extensions.takeIfIndex
import kotli.engine.template.FileRule
import kotli.engine.template.TemplateFile

/**
 * Replaces the entire block marked with a specific marker at the beginning and end of it.
 *
 * @param marker The text that must be presented in two lines to be considered as a block.
 * @param replacer A function providing the text to replace the found block.
 */
data class ReplaceMarkedBlock(
    private val marker: String,
    private val replacer: String,
    private val ignoreCase: Boolean = true
) : FileRule() {

    override fun doApply(file: TemplateFile) {
        val lines = file.lines
        val indexOfFirst = lines.indexOfFirst { isMarked(file, it, marker, ignoreCase) }.takeIfIndex() ?: return
        val indexOfLast = lines.indexOfLast { isMarked(file, it, marker, ignoreCase) }.takeIfIndex() ?: return
        if (indexOfFirst == indexOfLast) return
        val firstLine = lines[indexOfFirst]
        val tab = firstLine.substring(0, firstLine.indexOfFirst { it != ' ' }.takeIfIndex() ?: 0)
        val newBlock = replacer
        repeat(indexOfLast - indexOfFirst + 1) {
            lines.removeAt(indexOfFirst)
        }
        val newBlockLines = newBlock.lines().map { tab + it }
        lines.addAll(indexOfFirst, newBlockLines)
    }

}