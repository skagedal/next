package tech.skagedal.assistant.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.split
import tech.skagedal.assistant.Repository
import tech.skagedal.assistant.TaskResult
import tech.skagedal.assistant.services.GitReposService
import java.nio.file.FileSystem
import kotlin.system.exitProcess

private const val EXIT_NORMAL = 0
private const val CHANGE_DIRECTORY = 10

class GitReposCommand(
    private val fileSystem: FileSystem,
    private val gitReposService: GitReposService,
    private val repository: Repository,
) : CliktCommand(name = "git-repos") {
    val defaultBranches by option(
        "-b",
        metavar = "DEFAULTBRANCHES",
        help = "Comma-separated list of branches that should be checked out when we leave the task.  For each repo, these branches are tried in order."
    ).split(",")

    override fun run() {
        defaultBranches?.apply {
            forEach(::println)
        }
        val path = fileSystem.getPath(".").toAbsolutePath().normalize()
        val taskResult = gitReposService.handleAllGitRepos(path)

        val exitCode = when (taskResult) {
            TaskResult.Proceed -> EXIT_NORMAL
            TaskResult.ActionRequired -> EXIT_NORMAL
            is TaskResult.ShellActionRequired -> {
                repository.setRequestedDirectory(taskResult.directory)
                CHANGE_DIRECTORY
            }
        }
        exitProcess(exitCode)
    }
}