package kotli.engine.template.rule

import kotli.engine.template.FileRule
import kotli.engine.template.TemplateFile
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.exists

/**
 * Moves the given file from.
 *
 * @param targetName The target file name.
 */
data class MoveFile(
    private val sourceName: String,
    private val targetName: String
) : FileRule() {

    override fun doApply(file: TemplateFile) {
        val rootDir = file.path
        if (!rootDir.exists()) return

        val oldName = rootDir.resolve(sourceName)
        val newName = rootDir.resolve(targetName)
        if (oldName.exists()) {
            Files.createDirectories(newName)
            Files.move(oldName, newName, StandardCopyOption.REPLACE_EXISTING)
        }
    }
}