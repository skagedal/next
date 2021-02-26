package tech.skagedal.assistant

import org.springframework.stereotype.Repository
import java.io.IOException
import java.nio.file.FileAlreadyExistsException
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.time.Instant

/**
 * Keeps track of when things were run.
 */
@Repository
class Repository(
    val fileSystem: FileSystem
) {
    fun whenDidWeLastDo(task: String): java.time.Instant? {
        val path = pathForTask(task)
        try {
            return Files.getLastModifiedTime(path).toInstant()
        } catch (e: IOException) {
            return null
        }
    }

    fun weJustDid(task: String) {
        val path = pathForTask(task)
        Files.createDirectories(path.parent)
        try {
            Files.createFile(path)
        } catch (e: FileAlreadyExistsException) {
            // Ignored – that's fine
        }
        Files.setLastModifiedTime(path, FileTime.from(Instant.now()))
    }

    fun setRequestedDirectory(reqestedDirectory: Path) {
        val path = pathForRequestedDirectory()
        Files.createDirectories(path.parent)
        Files.writeString(path, reqestedDirectory.toString())
    }

    private fun pathForTask(task: String) = fileSystem.assistantDataDirectory().resolve("$task.task")
    private fun pathForRequestedDirectory() = fileSystem.assistantDataDirectory().resolve("requested-directory")
}