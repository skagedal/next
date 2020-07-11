package tech.skagedal.assistant.tasks

import tech.skagedal.assistant.RunnableTask
import tech.skagedal.assistant.TaskResult
import java.nio.file.Files
import java.nio.file.Path

class FileSystemLinterTask(
    val path: Path,
    val atThePlaceString: String,
    val pathFilter: (Path) -> Boolean
) : RunnableTask {
    override fun run(): TaskResult {
        val files = uncleanFiles()
        if (files.isNotEmpty()) {
            println("You have files laying around $atThePlaceString.")
            for (file in files) {
                println(file)
            }
            return TaskResult.ShellActionRequired(path)
        } else {
            println("No files laying around $atThePlaceString.")
            return TaskResult.Proceed
        }
    }

    private fun uncleanFiles(): List<Path> =
        Files.newDirectoryStream(path).use { stream ->
            stream.filter(pathFilter)
        }
}