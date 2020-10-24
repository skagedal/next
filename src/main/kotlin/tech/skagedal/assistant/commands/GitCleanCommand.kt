package tech.skagedal.assistant.commands

import com.github.ajalt.clikt.core.CliktCommand
import tech.skagedal.assistant.commands.GitCleanCommand.BranchAction.DELETE
import tech.skagedal.assistant.commands.GitCleanCommand.BranchAction.LOG
import tech.skagedal.assistant.commands.GitCleanCommand.BranchAction.NOTHING
import tech.skagedal.assistant.commands.GitCleanCommand.BranchAction.SHELL
import tech.skagedal.assistant.git.GitRepo
import tech.skagedal.assistant.ui.UserInterface
import java.nio.file.FileSystem

class GitCleanCommand(val fileSystem: FileSystem, val userInterface: UserInterface) : CliktCommand(name = "git-clean") {
    override fun run() {
        val repo = GitRepo(fileSystem.getPath("."))
        for (branch in repo.getNotMergedBranches()) {
            handleBranch(repo, branch)
        }
    }

    enum class BranchAction {
        DELETE, LOG, SHELL, NOTHING
    }

    fun handleBranch(repo: GitRepo, branch: String) {
        while (true) {
            when (pickBranchAction(branch)) {
                LOG ->
                    repo.showLog(branch)
                DELETE -> {
                    repo.deleteBranchForcefully(branch)
                    userInterface.reportActionTaken("Deleted $branch")
                    return
                }
                SHELL ->
                    repo.checkoutBranchAndEnterShell(branch)
                NOTHING ->
                    return
            }
        }
    }

    private fun pickBranchAction(branch: String): BranchAction {
        return userInterface.pickOne<BranchAction>("$branch is not merged to origin.") {
            choice(LOG, "Show git log")
            choice(DELETE, "Delete it")
            choice(SHELL, "Start a shell with branch checked out")
            choice(NOTHING, "Do nothing")
        }
    }
}
