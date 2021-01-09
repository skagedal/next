package tech.skagedal.assistant.commands

import com.github.ajalt.clikt.core.CliktCommand
import tech.skagedal.assistant.services.GitReposService
import java.nio.file.FileSystem

class GitReposCommand(
    private val fileSystem: FileSystem,
    private val gitReposService: GitReposService
) : CliktCommand(name = "git-repos") {
    override fun run() {
        val path = fileSystem.getPath(".")
        gitReposService.handleAllGitRepos(path)
    }
}