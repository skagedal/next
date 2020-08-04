package tech.skagedal.assistant.configuration

import com.fasterxml.jackson.databind.exc.ValueInstantiationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.StringReader

internal class ConfigurationLoaderTest {
    private val configurationLoader = ConfigurationLoader()

    @Test
    internal fun `tasks can be loaded`() {
        assertEquals(
            TasksFile(
                listOf(
                    Task.BrewUpgradeTask(WhenExpression.Always),
                    Task.FileSystemLintTask,
                    Task.GmailTask("foo@bar.com"),
                    Task.CustomTask("foo", "echo hello", WhenExpression.Always)
                )
            ),
            configurationLoader.loadTasks(
                """
                - task: brew-upgrade
                  when: always
                - task: file-system-lint
                - task: gmail
                  account: foo@bar.com
                - task: custom
                  id: foo
                  shell: "echo hello"
                  when: always
                """.trimIndent()
            )
        )
    }

    private fun ConfigurationLoader.loadTasks(str: String) = loadTasks(StringReader(str))
}