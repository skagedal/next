package tech.skagedal.next

import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path

class FileSystemLinter(
    val fileSystem: FileSystem
) {
    fun run() {
        checkCleanHome()
    }

    // Rule: We should not have non-hidden, non-directory files laying around in the home directory.

    private fun checkCleanHome() {
        val files = uncleanFilesInHome()
        if (files.isNotEmpty()) {
            println("You have files laying around in the home directory.  Remove them.")
        } else {
            println("All is well!")
        }
    }

    private fun uncleanFilesInHome(): List<Path> =
        Files
            .newDirectoryStream(fileSystem.home()).use { stream ->
                stream.filter { Files.isRegularFile(it) && !Files.isHidden(it) }
            }
}

fun FileSystem.home() = getPath(System.getProperty("user.home"))