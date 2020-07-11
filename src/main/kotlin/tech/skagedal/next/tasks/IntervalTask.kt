package tech.skagedal.next.tasks

import tech.skagedal.next.RunnableTask
import tech.skagedal.next.TaskRecords
import tech.skagedal.next.TaskResult
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class IntervalTask(
    val taskRecords: TaskRecords,
    val taskIdentifier: String,
    val doTheTask: () -> Unit
) : RunnableTask {
    override fun run(): TaskResult {
        val daysAgo = daysSinceWeLastDid(taskIdentifier)
        if (daysAgo == null) {
            println("We have never done $taskIdentifier, so let's do it now.")
            doTheTask()
            taskRecords.weJustDid(taskIdentifier)
        } else if (daysAgo > 7) {
            println("It was more than seven days since we did $taskIdentifier, so let's do it now.")
            doTheTask()
            taskRecords.weJustDid(taskIdentifier)
        } else {
            println("It was just $daysAgo days since we did $taskIdentifier, so let's not do it again.")
        }

        return TaskResult.Proceed
    }

    private fun daysSinceWeLastDid(task: String): Long? =
        taskRecords.whenDidWeLastDo(task)?.let {
            LocalDateTime.ofInstant(it, ZoneOffset.UTC).until(LocalDateTime.now(), ChronoUnit.DAYS)
        }

}