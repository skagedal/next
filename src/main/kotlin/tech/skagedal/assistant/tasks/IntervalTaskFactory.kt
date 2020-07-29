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

    private fun doBrewUpgrade() {
        processRunner.runBrewUpgrade()
    }
}