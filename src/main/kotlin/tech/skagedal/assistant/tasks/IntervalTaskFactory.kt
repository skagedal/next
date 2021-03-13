package tech.skagedal.assistant.tasks

import org.springframework.stereotype.Component
import tech.skagedal.assistant.ProcessRunner
import tech.skagedal.assistant.RunnableTask
import tech.skagedal.assistant.Repository
import tech.skagedal.assistant.configuration.WhenExpression
import tech.skagedal.assistant.pathWithShellExpansions
import tech.skagedal.assistant.duechecker.DueChecker
import java.nio.file.FileSystem

@Component
class IntervalTaskFactory(
    val fileSystem: FileSystem,
    val processRunner: ProcessRunner,
    val repository: Repository,
    val dueChecker: DueChecker
) {
    fun brewUpgradeTask(whenExpression: WhenExpression): RunnableTask {
        return IntervalTask(
            repository,
            whenExpression,
            "brew-upgrade",
            dueChecker,
            ::doBrewUpgrade
        )
    }

    fun customShellTask(shellCommand: String, taskIdentifier: String, whenExpression: WhenExpression, directory: String?): RunnableTask {
        return IntervalTask(
            repository,
            whenExpression,
            taskIdentifier,
            dueChecker
        ) {
            processRunner.runShellCommand(shellCommand, directory?.let { fileSystem.pathWithShellExpansions(it) })
        }
    }

    private fun doBrewUpgrade() {
        processRunner.runBrewUpgrade()
    }
}