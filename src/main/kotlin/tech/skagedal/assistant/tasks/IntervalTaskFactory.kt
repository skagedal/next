package tech.skagedal.assistant.tasks

import tech.skagedal.assistant.ProcessRunner
import tech.skagedal.assistant.RunnableTask
import tech.skagedal.assistant.Repository

class IntervalTaskFactory(
    val processRunner: ProcessRunner,
    val repository: Repository
) {
    fun brewUpgradeTask(): RunnableTask {
        return IntervalTask(
            repository,
            "brew-upgrade",
            ::doBrewUpgrade
        )
    }

    private fun doBrewUpgrade() {
        processRunner.runBrewUpgrade()
    }
}