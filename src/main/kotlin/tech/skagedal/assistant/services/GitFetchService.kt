package tech.skagedal.assistant.services

import tech.skagedal.assistant.general.filesInDirectory
import tech.skagedal.assistant.git.GitRepo
import tech.skagedal.assistant.ui.UserInterface
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path

class GitFetchService(private val fileSystem: FileSystem, private val userInterface: UserInterface) {
    fun fetchAllGitRepos(path: Path) =
        path.filesInDirectory()
            .filter(Files::isDirectory)
            .map(::GitRepo)
            .forEach(this::fetch)

    private fun fetch(repo: GitRepo) {
        println("Fetching ${repo.dir.fileName}...")
        repo.fetchAndPrune()
    }
}