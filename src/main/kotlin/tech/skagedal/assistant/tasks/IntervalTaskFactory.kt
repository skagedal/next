package tech.skagedal.assistant.tasks

import tech.skagedal.assistant.ProcessRunner
import tech.skagedal.assistant.RunnableTask
import tech.skagedal.assistant.Repository
import tech.skagedal.assistant.configuration.WhenExpression
import tech.skagedal.assistant.pathWithShellExpansions
import java.nio.file.FileSystem
import java.nio.file.Path

class IntervalTaskFactory(
    val fileSystem: FileSystem,
    val processRunner: ProcessRunner,
    val repository: Repository
) {
    fun brewUpgradeTask(whenExpression: WhenExpression): RunnableTask {
        return IntervalTask(
            repository,
            whenExpression,
            "brew-upgrade",
            ::doBrewUpgrade
        )
    }

    fun customShellTask(shellCommand: String, taskIdentifier: String, whenExpression: WhenExpression, directory: String?): RunnableTask {
        return IntervalTask(
            repository,
            whenExpression,
            taskIdentifier,
            {
                processRunner.runShellCommand(shellCommand, directory?.let { fileSystem.pathWithShellExpansions(it) })
            }
        )
    }

    private fun doBrewUpgrade() {
        processRunner.runBrewUpgrade()
    }
}