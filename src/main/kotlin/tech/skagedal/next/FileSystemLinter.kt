package tech.skagedal.next

import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path

class FileSystemLinter(
    val fileSystem: FileSystem,
    val processRunner: ProcessRunner
) {
    fun run() {
        checkCleanHome()
        checkCleanDesktop()
    }

    // Rule: We should not have non-hidden, non-directory files laying around in the home directory.

    private fun checkCleanHome() {
        checkCleanDirectory(fileSystem.home(), "in your home directory") {
            Files.isRegularFile(it) && !Files.isHidden(it)
        }
    }

    // Rule: We should not have files laying around on the Desktop.  The .DS_Store file is ok.

    private fun checkCleanDesktop() {
        checkCleanDirectory(fileSystem.desktop(), "on the Desktop") {
            Files.isRegularFile(it) && !it.fileName.toString().equals(".DS_Store", true)
        }
    }

    // Common stuff

    private fun checkCleanDirectory(
        path: Path,
        atThePlaceString: String,
        pathFilter: (Path) -> Boolean
    ) {
        val files = uncleanFilesInPath(path, pathFilter)

        if (files.isNotEmpty()) {
            println("You have files laying around $atThePlaceString.  Remove them, then exit subshell.")
            for (file in files) {
                println(file)
            }
            processRunner.runInteractiveShell(path)
        } else {
            println("No files laying around $atThePlaceString.")
        }
    }

    private fun uncleanFilesInPath(path: Path, pathFilter: (Path) -> Boolean): List<Path> =
        Files
            .newDirectoryStream(path).use { stream ->
                stream.filter(pathFilter)
            }

    private fun homeRules(path: Path): Boolean {
        return Files.isRegularFile(path) && !Files.isHidden(path)
    }
}

fun FileSystem.home() = getPath(System.getProperty("user.home"))
fun FileSystem.desktop() = home().resolve("Desktop")
