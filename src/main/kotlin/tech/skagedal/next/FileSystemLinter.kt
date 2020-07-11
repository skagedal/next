package tech.skagedal.next

import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path

class FileSystemLinter(
    val fileSystem: FileSystem,
    val shellStarter: ShellStarter
) {
    fun run() {
        checkCleanHome()
    }

    // Rule: We should not have non-hidden, non-directory files laying around in the home directory.

    private fun checkCleanHome() {
        checkCleanDirectory(fileSystem.home(), "the home directory")
    }

    private fun checkCleanDirectory(path: Path, placeDescriptionDefiniteArticle: String) {
        val files = uncleanFilesInPath(path)
        if (files.isNotEmpty()) {
            println("You have files laying around in $placeDescriptionDefiniteArticle.  Remove them, then exit subshell.")
            for (file in files) {
                println(file)
            }
            shellStarter.start(path)
        } else {
            println("All is well!")
        }
    }

    private fun uncleanFilesInPath(path: Path): List<Path> =
        Files
            .newDirectoryStream(path).use { stream ->
                stream.filter { Files.isRegularFile(it) && !Files.isHidden(it) }
            }
}

fun FileSystem.home() = getPath(System.getProperty("user.home"))