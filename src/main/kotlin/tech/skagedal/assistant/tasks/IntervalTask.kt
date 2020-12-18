package tech.skagedal.assistant.tasks

import org.slf4j.LoggerFactory
import tech.skagedal.assistant.ProcessExitedUnsuccessfullyException
import tech.skagedal.assistant.Repository
import tech.skagedal.assistant.RunnableTask
import tech.skagedal.assistant.TaskResult
import tech.skagedal.assistant.configuration.WhenExpression
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class IntervalTask(
    val repository: Repository,
    val whenExpression: WhenExpression,
    val taskIdentifier: String,
    val doTheTask: () -> Unit
) : RunnableTask {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun runTask(): TaskResult {
        val due = checkDue(repository.whenDidWeLastDo(taskIdentifier), Instant.now(), whenExpression)
        if (due.isDue) {
            logger.info("Performing $taskIdentifier")
            logger.debug("Performing $taskIdentifier, because ${due.reason}.")
            try {
                doTheTask()
            } catch (e: ProcessExitedUnsuccessfullyException) {
                return TaskResult.ActionRequired
            }
            repository.weJustDid(taskIdentifier)
        } else {
            logger.debug("Not performing $taskIdentifier, because ${due.reason}.")
        }

        return TaskResult.Proceed
    }

    data class Due(
        val isDue: Boolean,
        val reason: String
    )

    fun checkDue(instantWhenDone: Instant?, instantNow: Instant, whenExpression: WhenExpression) =
        if (instantWhenDone == null) {
            Due(true, "it has never been done before")
        } else when(whenExpression) {
            WhenExpression.Never -> Due(false, "it should never be done")
            WhenExpression.Always -> Due(true, "it should always be done")
            is WhenExpression.EveryNDays -> {
                val then = LocalDateTime.ofInstant(instantWhenDone, ZoneOffset.UTC)
                val now = LocalDateTime.ofInstant(instantNow, ZoneOffset.UTC)
                val diff = then.until(now, ChronoUnit.DAYS)
                if (diff >= whenExpression.n) {
                    Due(true, "${whenExpression.n} or more days have passed")
                } else if (diff == 0L) {
                    Due(false, "it has been done today already")
                } else {
                    Due(false, "only $diff days have passed")
                }
            }
        }
}