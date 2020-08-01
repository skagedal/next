package tech.skagedal.assistant.tasks

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import tech.skagedal.assistant.RunnableTask
import tech.skagedal.assistant.TaskResult
import tech.skagedal.assistant.isGloballyIgnored
import java.nio.file.Files
import java.nio.file.Path

class GitReposTask(val path: Path) : RunnableTask {
    enum class GitResult {
        CLEAN, DIRTY, NOT_A_GIT_REPOSITORY, NOT_A_DIRECTORY
    }

    data class ResultWithPath(
        val path: Path,
        val result: GitResult
    )

    override fun runTask() = fetchAllResults()
        .find { it.result != GitResult.CLEAN }?.let {
            when (it.result) {
                GitResult.DIRTY -> {
                    System.err.println("Dirty git repository")
                    TaskResult.ShellActionRequired(it.path)
                }
                GitResult.NOT_A_GIT_REPOSITORY -> {
                    System.err.println("Not a git repository")
                    TaskResult.ShellActionRequired(it.path)
                }
                GitResult.NOT_A_DIRECTORY -> {
                    System.err.println("Not a directory: ${it.path}")
                    TaskResult.ShellActionRequired(path)
                }
                GitResult.CLEAN -> throw IllegalStateException()
            }
        } ?: TaskResult.Proceed

    private fun fetchAllResults() =
        runBlocking {
            filesInDirectory(path).map {
                async(Dispatchers.IO) {
                    ResultWithPath(it, repoResult(it))
                }
            }.awaitAll()
        }

    private fun repoResult(dir: Path): GitResult {
        if (!Files.isDirectory(dir)) {
            return if (path.isGloballyIgnored()) GitResult.CLEAN else GitResult.NOT_A_DIRECTORY
        }
        val process = ProcessBuilder("git", "status", "--porcelain", "-unormal")
            .directory(dir.toFile())
            .start()
        val exitCode = process.waitFor()
        if (exitCode != 0) {
            return GitResult.NOT_A_GIT_REPOSITORY
        }
        val bytes = process
            .inputStream
            .readAllBytes()
        return if (bytes.size > 0) GitResult.DIRTY else GitResult.CLEAN
    }
}

fun filesInDirectory(path: Path): List<Path> = Files.newDirectoryStream(path).use { it.toList() }
