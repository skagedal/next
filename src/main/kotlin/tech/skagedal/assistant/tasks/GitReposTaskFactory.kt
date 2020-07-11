package tech.skagedal.assistant.tasks

import tech.skagedal.assistant.RunnableTask
import tech.skagedal.assistant.pathWithShellExpansions
import java.nio.file.FileSystem
import java.util.regex.Matcher

class GitReposTaskFactory(
    val fileSystem: FileSystem
) {
    fun task(directory: String): RunnableTask {
        return GitReposTask(fileSystem.pathWithShellExpansions(directory))
    }
}