package tech.skagedal.assistant.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.split
import tech.skagedal.assistant.services.GitReposService
import java.nio.file.FileSystem

class GitReposCommand(
    private val fileSystem: FileSystem,
    private val gitReposService: GitReposService
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
        val path = fileSystem.getPath(".")
        gitReposService.handleAllGitRepos(path)
    }
}