package tech.skagedal.assistant.configuration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.StringReader

internal class ConfigurationLoaderTest {
    @Test
    internal fun `tasks can be loaded`() {
        val configurationLoader = ConfigurationLoader()
        assertEquals(
            TasksFile(
                listOf(
                    Task.BrewUpgradeTask,
                    Task.FileSystemLintTask,
                    Task.GmailTask("foo@bar.com")
                )
            ),
            configurationLoader.loadTasks(
                """
                - task: brew-upgrade
                - task: file-system-lint
                - task: gmail
                  account: foo@bar.com
                """.trimIndent()
            )
        )
    }

    private fun ConfigurationLoader.loadTasks(str: String) = loadTasks(StringReader(str))
}