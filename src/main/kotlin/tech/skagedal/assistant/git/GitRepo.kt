package tech.skagedal.assistant.git

import java.nio.file.Path

class GitRepo(val dir: Path) {

    fun getNotMergedBranches(): List<String> {
        return git("git", "branch", "--format=%(refname:short)", "--no-merged").linesExceptTrailing()
    }

    fun getAllRemoteBranches(): List<String> {
        return git("git", "branch", "--remotes", "--format=%(refname:short)").linesExceptTrailing()
    }

    fun deleteBranchForcefully(branch: String) {
        git("git", "branch", "-D", branch)
    }

    fun showLog(branch: String) {
        runInteractive("tig", branch)
    }

    fun checkoutBranchAndEnterShell(branch: String) {
        git("git", "checkout", branch)
        runInteractive("zsh")
    }

    private fun git(vararg command: String): String {
        val process = ProcessBuilder(*command)
            .directory(dir.toFile())
            .start()
        val exitCode = process.waitFor()
        val output = process.inputStream
            .readAllBytes()
            .toString(Charsets.UTF_8)
        if (exitCode != 0) {
            throw NonZeroGitExitCode(exitCode, output)
        }
        return output
    }

    private fun runInteractive(vararg command: String) {
        val process = ProcessBuilder(*command)
            .directory(dir.toFile())
            .inheritIO()
            .start()
        process.waitFor()
    }
    private fun CharSequence.linesExceptTrailing() = lines().dropLastWhile { it.isEmpty() }
}