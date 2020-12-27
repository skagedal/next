package tech.skagedal.assistant.commands

import com.github.ajalt.clikt.core.CliktCommand
import tech.skagedal.assistant.commands.GitCleanCommand.BranchAction.DELETE
import tech.skagedal.assistant.commands.GitCleanCommand.BranchAction.LOG
import tech.skagedal.assistant.commands.GitCleanCommand.BranchAction.NOTHING
import tech.skagedal.assistant.commands.GitCleanCommand.BranchAction.PUSH
import tech.skagedal.assistant.commands.GitCleanCommand.BranchAction.PUSH_CREATING_ORIGIN
import tech.skagedal.assistant.commands.GitCleanCommand.BranchAction.REBASE
import tech.skagedal.assistant.commands.GitCleanCommand.BranchAction.SHELL
import tech.skagedal.assistant.git.Branch
import tech.skagedal.assistant.git.GitRepo
import tech.skagedal.assistant.git.UpstreamStatus.IDENTICAL
import tech.skagedal.assistant.git.UpstreamStatus.LOCAL_IS_AHEAD_OF_UPSTREAM
import tech.skagedal.assistant.git.UpstreamStatus.MERGE_NEEDED
import tech.skagedal.assistant.git.UpstreamStatus.UPSTREAM_IS_AHEAD_OF_LOCAL
import tech.skagedal.assistant.git.UpstreamStatus.UPSTREAM_IS_GONE
import tech.skagedal.assistant.ui.UserInterface
import java.nio.file.FileSystem

class GitCleanCommand(val fileSystem: FileSystem, val userInterface: UserInterface) : CliktCommand(name = "git-clean") {
    override fun run() {
        val repo = GitRepo(fileSystem.getPath("."))
        handle(repo, repo.getBranches())
    }

    enum class BranchAction(val description: String) {
        PUSH("Push to origin"),
        PUSH_CREATING_ORIGIN("Push to create origin"),
        REBASE("Rebase onto origin"),
        DELETE("Delete it"),
        LOG("Show git log"),
        SHELL("Start a shell with branch checked out"),
        NOTHING("Do nothing")
    }

    fun handle(repo: GitRepo, branches: List<Branch>) {
        branches.forEach { handle(repo, it) }
    }

    fun handle(repo: GitRepo, branch: Branch) {
        branch.upstream?.let { upstream ->
            when (upstream.status) {
                IDENTICAL ->
                    return
                UPSTREAM_IS_AHEAD_OF_LOCAL ->
                    repo.rebase(branch.refname, upstream.name)
                LOCAL_IS_AHEAD_OF_UPSTREAM ->
                    selectAction(
                        repo, branch, "Branch is ahead of upstream", listOf(
                            PUSH, LOG, SHELL, NOTHING
                        )
                    )
                MERGE_NEEDED ->
                    selectAction(
                        repo, branch, "Different commits on local and upstream", listOf(
                            REBASE, LOG, DELETE, SHELL, NOTHING
                        )
                    )
                UPSTREAM_IS_GONE ->
                    selectAction(repo, branch, "Upstream is set, but it is gone", listOf(
                        DELETE, LOG, SHELL, NOTHING
                    ))
            }
        } ?: selectAction(repo, branch, "Branch has no upstream", listOf(
            PUSH_CREATING_ORIGIN, DELETE, LOG, SHELL, NOTHING
        ))
    }

    private fun selectAction(repo: GitRepo, branch: Branch, message: String, actions: List<BranchAction>) {
        var actionIsHandled = false
        while (!actionIsHandled) {
            val action = userInterface.pickOne<BranchAction>("${branch.refname}: $message") {
                for (action in actions) {
                    choice(action, action.description)
                }
            }
            actionIsHandled = performAction(repo, branch, action)
        }
    }

    private fun performAction(repo: GitRepo, branch: Branch, action: BranchAction): Boolean =
        when (action) {
            PUSH -> {
                repo.push(branch.refname)
                true
            }
            PUSH_CREATING_ORIGIN -> {
                repo.pushCreatingOrigin(branch.refname)
                true
            }
            REBASE -> {
                repo.rebase(branch.refname, branch.upstream!!.name)
                true
            }
            DELETE -> {
                repo.deleteBranchForcefully(branch.refname)
                true
            }
            LOG -> {
                repo.showLog(branch.refname)
                false
            }
            SHELL -> {
                repo.checkoutBranchAndEnterShell(branch.refname)
                false
            }
            NOTHING -> true
        }
}
