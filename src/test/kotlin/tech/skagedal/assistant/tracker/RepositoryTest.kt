package tech.skagedal.assistant.tracker

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.FileSystems
import java.time.LocalDate

internal class RepositoryTest {
    private val restoredHome = System.getProperty("user.home")

    @BeforeEach
    internal fun setUp() {
        System.setProperty("user.home", "/home/the-user")
    }

    @AfterEach
    internal fun tearDown() {
        System.setProperty("user.home", restoredHome)
    }

    @Test
    internal fun `test file name formatting`() {
        val repository = Repository(FileSystems.getDefault())

        val p = repository.pathForWeekTrackerFile(LocalDate.of(2020, 7, 11))
        assertEquals("/home/the-user/.simons-assistant/data/tracker/2020-W28.txt", p.toString())
    }

    @Test
    internal fun `week numbers are padded with zeros`() {
        val repository = Repository(FileSystems.getDefault())

        val p = repository.pathForWeekTrackerFile(LocalDate.of(2020, 1, 4))
        assertEquals("/home/the-user/.simons-assistant/data/tracker/2020-W01.txt", p.toString())
    }

    @Test
    internal fun `week-based year is used`() {
        val repository = Repository(FileSystems.getDefault())

        val p = repository.pathForWeekTrackerFile(LocalDate.of(2019, 12, 30))
        assertEquals("/home/the-user/.simons-assistant/data/tracker/2020-W01.txt", p.toString())
    }

    @Test
    internal fun `create a default document`() {
        val repository = Repository(FileSystems.getDefault())
        val x = repository.defaultDocument(LocalDate.of(2020, 7, 11))
        assertEquals(null, x)
    }
}