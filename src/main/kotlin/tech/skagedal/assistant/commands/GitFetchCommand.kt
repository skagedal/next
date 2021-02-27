package tech.skagedal.assistant.commands

import com.github.ajalt.clikt.core.CliktCommand
import org.springframework.stereotype.Component
import tech.skagedal.assistant.ioc.Subcommand
import tech.skagedal.assistant.services.GitFetchService
import java.nio.file.FileSystem

@Subcommand
class GitFetchCommand(
    private val fileSystem: FileSystem,
    private val gitFetchService: GitFetchService
) : CliktCommand(name = "git-fetch") {
    override fun run() {
        val path = fileSystem.getPath(".")
        gitFetchService.fetchAllGitRepos(path)
    }
}
