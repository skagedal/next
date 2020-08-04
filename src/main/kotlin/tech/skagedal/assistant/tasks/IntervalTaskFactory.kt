package tech.skagedal.assistant.tasks

import tech.skagedal.assistant.ProcessRunner
import tech.skagedal.assistant.RunnableTask
import tech.skagedal.assistant.Repository
import tech.skagedal.assistant.configuration.WhenExpression

class IntervalTaskFactory(
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

    fun customShellTask(shellCommand: String, taskIdentifier: String, whenExpression: WhenExpression): RunnableTask {
        return IntervalTask(
            repository,
            whenExpression,
            taskIdentifier,
            {
                processRunner.runShellCommand(shellCommand)
            }
        )
    }

    private fun doBrewUpgrade() {
        processRunner.runBrewUpgrade()
    }
}