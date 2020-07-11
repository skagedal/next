package tech.skagedal.assistant.configuration

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "task"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Task.BrewUpgradeTask::class, name = "brew-upgrade"),
    JsonSubTypes.Type(value = Task.FileSystemLintTask::class, name = "file-system-lint"),
    JsonSubTypes.Type(value = Task.GmailTask::class, name = "gmail")
)
sealed class Task {
    object BrewUpgradeTask : Task()
    object FileSystemLintTask : Task()
    data class GmailTask(
        val account: String
    ) : Task()
}

data class TasksFile(
    val tasks: List<Task>
)