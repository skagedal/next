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
                    Task.GmailTask("foo@bar.com")
                )
            ),
            configurationLoader.loadTasks(
                """
                - task: brew-upgrade
                  when: always
                - task: file-system-lint
                - task: gmail
                  account: foo@bar.com
                """.trimIndent()
            )
        )
    }

    @Test
    internal fun `when expressions are parsed correctly`() {
        assertEquals(WhenExpression.Always, configurationLoader.parseWhenExpression("always"))
        assertEquals(WhenExpression.Never, configurationLoader.parseWhenExpression("never"))
        assertEquals(WhenExpression.EveryNDays(1), configurationLoader.parseWhenExpression("daily"))
        assertThrows<ValueInstantiationException> { configurationLoader.parseWhenExpression("asdf") }
    }

    private fun ConfigurationLoader.loadTasks(str: String) = loadTasks(StringReader(str))
}