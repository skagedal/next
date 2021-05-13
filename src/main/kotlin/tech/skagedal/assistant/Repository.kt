package tech.skagedal.assistant

import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path

class Repository(
    val fileSystem: FileSystem
) {
    fun setRequestedDirectory(reqestedDirectory: Path) {
        val path = pathForRequestedDirectory()
        Files.createDirectories(path.parent)
        Files.writeString(path, reqestedDirectory.toString())
    }

    private fun pathForRequestedDirectory() = fileSystem.assistantDataDirectory().resolve("requested-directory")
}