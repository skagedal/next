package tech.skagedal.assistant

import java.io.IOException
import java.nio.file.FileAlreadyExistsException
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.attribute.FileTime
import java.time.Instant

/**
 * Keeps track of when things were run.
 */
class TaskRecords(
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

    private fun pathForTask(task: String) = fileSystem.assistantDataDirectory().resolve("$task.task")
}