package tech.skagedal.assistant

import java.nio.file.Path

sealed class TaskResult {
    object Proceed: TaskResult()
    object ActionRequired: TaskResult()
    data class ShellActionRequired(val directory: Path): TaskResult()
}