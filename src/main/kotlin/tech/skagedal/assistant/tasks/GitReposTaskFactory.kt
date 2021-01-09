package tech.skagedal.assistant.tasks

import tech.skagedal.assistant.RunnableTask
import tech.skagedal.assistant.pathWithShellExpansions
import tech.skagedal.assistant.services.GitReposService
import java.nio.file.FileSystem
import java.util.regex.Matcher

class GitReposTaskFactory(
    private val fileSystem: FileSystem,
    private val gitReposService: GitReposService
) {
    fun task(directory: String): RunnableTask {
        return GitReposTask(fileSystem.pathWithShellExpansions(directory), gitReposService)
    }
}