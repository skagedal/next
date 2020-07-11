package tech.skagedal.next.tasks

import tech.skagedal.next.ProcessRunner
import tech.skagedal.next.TaskRecords
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class IntervalTaskRunner(
    val processRunner: ProcessRunner,
    val taskRecords: TaskRecords
) {
    fun run() {
        val task = "brew-upgrade"
        val daysAgo = daysSinceWeLastDid(task)
        if (daysAgo == null) {
            println("We have never done $task, so let's do it now.")
            doBrewUpgrade(task)
        } else if (daysAgo > 7) {
            println("It was more than seven days since we did $task, so let's do it now.")
            doBrewUpgrade(task)
        } else {
            println("It was just $daysAgo days since we did $task, so let's not do it again.")
        }
    }

    private fun doBrewUpgrade(task: String) {
        processRunner.runBrewUpgrade()
        taskRecords.weJustDid(task)
    }

    private fun daysSinceWeLastDid(task: String): Long? =
        taskRecords.whenDidWeLastDo(task)?.let {
            LocalDateTime.ofInstant(it, ZoneOffset.UTC).until(LocalDateTime.now(), ChronoUnit.DAYS)
        }
}