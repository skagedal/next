package tech.skagedal.next.tasks

import tech.skagedal.next.ProcessRunner
import tech.skagedal.next.RunnableTask
import tech.skagedal.next.TaskRecords
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

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