package tech.skagedal.assistant.tasks

import tech.skagedal.assistant.RunnableTask
import tech.skagedal.assistant.TaskResult
import tech.skagedal.assistant.isGloballyIgnored
import java.lang.RuntimeException
import java.nio.file.Files
import java.nio.file.Path

class GitReposTask(val path: Path) : RunnableTask {
    override fun run(): TaskResult {
        Files.newDirectoryStream(path).use { stream ->
            for (p in stream) {
                if (Files.isDirectory(p)) {
                    try {
                        if (gitRepoIsDirty(p)) {
                            System.err.println("Dirty git repository")
                            return TaskResult.ShellActionRequired(p)
                        }
                    } catch (e: NotGitRepository) {
                        System.err.println("Not a git repository")
                        return TaskResult.ShellActionRequired(p)
                    }
                } else if (!p.isGloballyIgnored()){
                    System.err.println("Non-directory file $p laying around in code directory.  Not ok. ")
                    return TaskResult.ShellActionRequired(path)
                }
            }
        }
        return TaskResult.Proceed
    }

    private fun testPath(dir: Path) {
        gitRepoIsDirty(dir)
    }

    private fun gitRepoIsDirty(dir: Path): Boolean {
        val process = ProcessBuilder("git", "status", "--porcelain", "-unormal")
            .directory(dir.toFile())
            .start()
        val exitCode = process.waitFor()
        if (exitCode != 0) {
            throw NotGitRepository
        }
        val bytes = process
            .inputStream
            .readAllBytes()
        return bytes.size > 0
    }

    object NotGitRepository: RuntimeException()
}