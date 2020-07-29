package tech.skagedal.assistant.configuration

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.lang.RuntimeException

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "task"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Task.BrewUpgradeTask::class, name = "brew-upgrade"),
    JsonSubTypes.Type(value = Task.FileSystemLintTask::class, name = "file-system-lint"),
    JsonSubTypes.Type(value = Task.GmailTask::class, name = "gmail"),
    JsonSubTypes.Type(value = Task.GitReposTask::class, name = "git-repos")
)
sealed class Task {
    data class BrewUpgradeTask(
        @JsonProperty("when")
        val whenExpression: WhenExpression
    ) : Task()

    object FileSystemLintTask : Task()
    data class GmailTask(
        val account: String
    ) : Task()

    data class GitReposTask(
        val directory: String
    ) : Task()
}

sealed class WhenExpression {
    object Never : WhenExpression()
    object Always : WhenExpression()
    data class EveryNDays(
        val n: Int
    ) : WhenExpression()

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromString(string: String): WhenExpression {
            return when (string) {
                "never" -> Never
                "always" -> Always
                "daily" -> EveryNDays(1)
                else -> throw UnknownWhenString(
                    "Can't parse $string as a \"when\" string",
                    string
                )
            }
        }
    }
}

data class TasksFile(
    val tasks: List<Task>
)

class UnknownWhenString(message: String, val whenString: String): RuntimeException(message)