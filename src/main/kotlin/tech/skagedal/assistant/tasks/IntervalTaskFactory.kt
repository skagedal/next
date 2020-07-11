package tech.skagedal.assistant.tasks

import tech.skagedal.assistant.ProcessRunner
import tech.skagedal.assistant.RunnableTask
import tech.skagedal.assistant.TaskRecords

class IntervalTaskFactory(
    val processRunner: ProcessRunner,
    val taskRecords: TaskRecords
) {
    fun brewUpgradeTask(): RunnableTask {
        return IntervalTask(
            taskRecords,
            "brew-upgrade",
            ::doBrewUpgrade
        )
    }

    private fun doBrewUpgrade() {
        processRunner.runBrewUpgrade()
    }
}