package tech.skagedal.assistant.commands

import com.github.ajalt.clikt.core.CliktCommand
import org.slf4j.LoggerFactory
import tech.skagedal.assistant.Repository
import tech.skagedal.assistant.RunnableTask
import tech.skagedal.assistant.TaskResult
import tech.skagedal.assistant.configuration.ConfigurationLoader
import tech.skagedal.assistant.configuration.Task
import tech.skagedal.assistant.configuration.TasksFile
import tech.skagedal.assistant.tasks.EstablishWorkOrHobbyTask
import tech.skagedal.assistant.tasks.FileSystemLinterTaskFactory
import tech.skagedal.assistant.tasks.GitReposTaskFactory
import tech.skagedal.assistant.tasks.GmailCheckerTaskFactory
import tech.skagedal.assistant.tasks.IntervalTaskFactory
import tech.skagedal.assistant.tasksYmlFile
import java.nio.file.FileSystem
import java.nio.file.Files
import kotlin.system.exitProcess

private const val EXIT_NORMAL = 0
private const val EXIT_ERROR = 1
private const val CHANGE_DIRECTORY = 10

class Next(
    val fileSystem: FileSystem,
    val repository: Repository,
    val configurationLoader: ConfigurationLoader,
    val fileSystemLinterTaskFactory: FileSystemLinterTaskFactory,
    val intervalTaskFactory: IntervalTaskFactory,
    val gmailCheckerTaskFactory: GmailCheckerTaskFactory,
    val gitReposTaskFactory: GitReposTaskFactory
) : CliktCommand() {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun run() {
        exitProcess(runCommand())
    }

    fun runCommand(): Int {
        val pass = {}
        val tasks = readTasks() ?: return 1
        for (task in runnableTasks(tasks.tasks)) {
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
                is Task.CustomTask -> listOf(intervalTaskFactory.customShellTask(
                    task.shell, task.id, task.whenExpression, task.directory
                ))
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



