package tech.skagedal.assistant.commands

import ch.qos.logback.classic.LoggerContext
import com.github.ajalt.clikt.core.CliktCommand
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import tech.skagedal.assistant.Repository
import tech.skagedal.assistant.RunnableTask
import tech.skagedal.assistant.TaskResult
import tech.skagedal.assistant.configuration.ConfigurationLoader
import tech.skagedal.assistant.configuration.Task
import tech.skagedal.assistant.configuration.TasksFile
import tech.skagedal.assistant.ioc.Subcommand
import tech.skagedal.assistant.tasks.EstablishWorkOrHobbyTask
import tech.skagedal.assistant.tasks.FileSystemLinterTaskFactory
import tech.skagedal.assistant.tasks.GitReposTaskFactory
import tech.skagedal.assistant.tasks.GmailCheckerTaskFactory
import tech.skagedal.assistant.tasks.IntervalTaskFactory
import tech.skagedal.assistant.tasksYmlFile
import tech.skagedal.assistant.ui.UserInterface
import java.nio.file.FileSystem
import java.nio.file.Files
import kotlin.system.exitProcess

private const val EXIT_NORMAL = 0
private const val EXIT_ERROR = 1
private const val CHANGE_DIRECTORY = 10

@Subcommand
class NextCommand(
    val fileSystem: FileSystem,
    val userInterface: UserInterface,
    val repository: Repository,
    val configurationLoader: ConfigurationLoader,
    val fileSystemLinterTaskFactory: FileSystemLinterTaskFactory,
    val intervalTaskFactory: IntervalTaskFactory,
    val gmailCheckerTaskFactory: GmailCheckerTaskFactory,
    val gitReposTaskFactory: GitReposTaskFactory
) : CliktCommand(name = "next") {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun run() {
        logger.info("Running next command")
        val exitResult = runCommand()
        logger.info("next command completed, exiting with code {}", exitResult)
        (LoggerFactory.getILoggerFactory() as LoggerContext).executorService.shutdown()
        exitProcess(exitResult)
    }

    fun runCommand(): Int {
        val pass = {}
        val tasks = readTasks()
        if (tasks == null) {
            userInterface.reportError("There was an error with the config file.")
            return 1
        }

        for (task in runnableTasks(tasks.tasks)) {
            logger.info("Running task {}", task)
            val result = task.runTask()
            when (result) {
                TaskResult.Proceed -> pass()
                TaskResult.ActionRequired -> return EXIT_NORMAL
                is TaskResult.ShellActionRequired -> {
                    repository.setRequestedDirectory(result.directory)
                    return CHANGE_DIRECTORY
                }
            }
        }
        return EXIT_NORMAL
    }

    fun runnableTasks(tasks: List<Task>): List<RunnableTask> {
        return tasks.flatMap { task ->
            when (task) {
                Task.FileSystemLintTask -> fileSystemLinterTaskFactory.standardTasks()
                Task.EstablishWorkOrHobbyTask -> listOf(EstablishWorkOrHobbyTask())
                is Task.CustomTask -> listOf(
                    intervalTaskFactory.customShellTask(
                        task.shell, task.id, task.whenExpression, task.directory
                    )
                )
                is Task.BrewUpgradeTask -> listOf(intervalTaskFactory.brewUpgradeTask(task.whenExpression))
                is Task.GmailTask -> listOf(gmailCheckerTaskFactory.task(task.account))
                is Task.GitReposTask -> listOf(gitReposTaskFactory.task(task.directory))
            }
        }
    }

    private fun readTasks(): TasksFile? {
        val tasksFile = fileSystem.tasksYmlFile()
        return Files.newBufferedReader(tasksFile).use { reader ->
            try {
                configurationLoader.loadTasks(reader)
            } catch (exception: ConfigurationLoader.BadConfigurationFormat) {
                logger.error("There was something wrong with $tasksFile", exception)
                null
            }
        }
    }
}


