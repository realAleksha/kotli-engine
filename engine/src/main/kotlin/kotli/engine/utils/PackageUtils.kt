package kotli.engine.utils

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.exists
import kotlin.io.path.readLines
import kotlin.io.path.writeText

/**
 * Common utilities for operations with packages.
 */
object PackageUtils {

    /**
     * Renames the #oldPackageName to #newPackageName under the given root folder #rootDir.
     * All subsequent files are moved to the new package name accordingly.
     *
     * @param rootDir The root directory where the package renaming operation will be performed.
     * @param oldPackageName The old package name to be replaced.
     * @param newPackageName The new package name to replace the old package name.
     */
    fun rename(rootDir: Path, oldPackageName: String, newPackageName: String) {
        if (!rootDir.exists()) return

        val oldPackagePath = oldPackageName.replace('.', '/')
        val newPackagePath = newPackageName.replace('.', '/')

        val oldPackageDir = rootDir.resolve(oldPackagePath)
        val newPackageDir = rootDir.resolve(newPackagePath)

        if (oldPackageDir.exists()) {
            Files.createDirectories(newPackageDir)
            Files.move(oldPackageDir, newPackageDir, StandardCopyOption.REPLACE_EXISTING)
        }

        Files.walk(rootDir)
            .filter { Files.isRegularFile(it) }
            .forEach { filePath ->
                val fileName = filePath.fileName.toString()
                if (fileName.endsWith(".java") || fileName.endsWith(".kt")) {
                    updatePackageReferences(filePath, oldPackageName, newPackageName)
                }
            }
    }

    private fun updatePackageReferences(
        filePath: Path,
        oldPackageName: String,
        newPackageName: String
    ) {
        val text = filePath
            .readLines()
            .joinToString("\n") { line ->
                line
                    .replace("${oldPackageName}.", "${newPackageName}.")
                    .replace("package $oldPackageName", "package $newPackageName")
            }
        filePath.writeText(text)
    }

}