package tech.skagedal.assistant.tasks

import tech.skagedal.assistant.RunnableTask
import tech.skagedal.assistant.services.GitReposService
import java.nio.file.Path

class GitReposTask(
    val path: Path,
    val gitReposService: GitReposService
) : RunnableTask {
    override fun runTask() = gitReposService.handleAllGitRepos(path)
}

