package tech.skagedal.assistant.tasks

import org.slf4j.LoggerFactory
import tech.skagedal.assistant.ProcessExitedUnsuccessfullyException
import tech.skagedal.assistant.Repository
import tech.skagedal.assistant.RunnableTask
import tech.skagedal.assistant.TaskResult
import tech.skagedal.assistant.configuration.WhenExpression
import tech.skagedal.assistant.duechecker.DueChecker
import java.time.Instant

class IntervalTask(
    val repository: Repository,
    val whenExpression: WhenExpression,
    val taskIdentifier: String,
    val dueChecker: DueChecker,
    val doTheTask: () -> Unit
) : RunnableTask {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun runTask(): TaskResult {
        val due = dueChecker.check(repository.whenDidWeLastDo(taskIdentifier), Instant.now(), whenExpression)
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

}