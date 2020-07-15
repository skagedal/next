package tech.skagedal.assistant.tracker

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.FileSystems
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.assertTrue

internal class TimeTrackerTest {
    @org.junit.jupiter.api.Test
    internal fun `test that TimeTracker calculates time spans correctly`() {
        val timeTracker = createTimeTracker()
        assertEquals(
            60,
            timeTracker.minutesForLine(
                Line.ClosedShift(
                    LocalTime.of(11, 0),
                    LocalTime.of(12, 0)
                ),
                LocalTime.of(0, 0)
            )
        )

        assertEquals(
            60 * 8,
            timeTracker.minutesForLine(
                Line.SpecialDay("vacation"),
                LocalTime.of(0, 0)
            )
        )
    }

    @Test
    internal fun `test that TimeTracker sums up days correctly`() {
        val timeTracker = createTimeTracker()
        val minutes = timeTracker.minutesForDay(
            Day(
                LocalDate.of(2020, 1, 1),
                listOf(
                    Line.ClosedShift(
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0)
                    ),
                    Line.SpecialShift(
                        "vab",
                        LocalTime.of(10, 0),
                        LocalTime.of(11, 0)
                    ),
                    Line.OpenShift(
                        LocalTime.of(13, 0)
                    )
                )
            ),
            LocalTime.of(15, 0)
        )
        assertEquals(60 + 60 + 120, minutes)
    }

    @Test
    internal fun `test that TimeTracker gives correct week reports`() {
        val timeTracker = createTimeTracker()
        val yesterday = LocalDate.of(2020, 7, 1)
        val today = LocalDate.of(2020, 7, 2)
        val currentTime = LocalTime.of(15, 0)
        val document = Document(
            emptyList(),
            listOf(
                Day(
                    yesterday,
                    listOf(
                        Line.SpecialDay("vab")
                    )
                ),
                Day(
                    today,
                    listOf(
                        Line.ClosedShift(
                            LocalTime.of(8, 10),
                            LocalTime.of(8, 30)
                        ),
                        Line.ClosedShift(
                            LocalTime.of(8, 50),
                            LocalTime.of(9, 10)
                        ),
                        Line.OpenShift(
                            LocalTime.of(13, 30)
                        )
                    )
                )
            )
        )
        val weekReport = timeTracker.weekReportForDateInDocument(
            document,
            today,
            currentTime
        )
        assertEquals(
            8 * 60 + 20 + 20 + 90,
            weekReport.minutesThisWeek
        )
        assertEquals(
            20 + 20 + 90,
            weekReport.minutesToday
        )
        assertTrue(weekReport.isOngoing)
    }

    private fun createTimeTracker(): TimeTracker {
        val serializer = Serializer()
        val timeTracker = TimeTracker(
            Repository(FileSystems.getDefault(), serializer),
            serializer,
            60 * 8
        )
        return timeTracker
    }
}