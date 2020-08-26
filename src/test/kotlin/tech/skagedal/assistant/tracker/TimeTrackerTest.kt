package tech.skagedal.assistant.tracker

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.file.FileSystems
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.assertTrue

internal class TimeTrackerTest {
    @org.junit.jupiter.api.Test
    internal fun `test that TimeTracker calculates time spans correctly`() {
        val timeTracker = createTimeTracker()
        assertEquals(
            Duration.ofMinutes(60),
            timeTracker.trackedDurationForLine(
                Line.ClosedShift(
                    LocalTime.of(11, 0),
                    LocalTime.of(12, 0)
                ),
                LocalTime.of(0, 0)
            )
        )

        assertEquals(
            Duration.ofMinutes(60 * 8),
            timeTracker.trackedDurationForLine(
                Line.SpecialDay("vacation"),
                LocalTime.of(0, 0)
            )
        )
    }

    @Test
    internal fun `test that TimeTracker sums up days correctly`() {
        val timeTracker = createTimeTracker()
        val duration = timeTracker.trackedDurationForDay(
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
        assertEquals(Duration.ofMinutes(60 + 60 + 120), duration)
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
            Duration.ofMinutes(8 * 60 + 20 + 20 + 90),
            weekReport.durationThisWeek
        )
        assertEquals(
            Duration.ofMinutes(20 + 20 + 90),
            weekReport.durationToday
        )
        assertTrue(weekReport.isOngoing)
    }

    @Test
    internal fun `test that we can start a new shift in an empty document`() {
        val timeTracker = createTimeTracker()
        val document = Document(
            emptyList(),
            emptyList()
        )
        val newDocument = timeTracker.documentWithTrackingStarted(
            document,
            LocalDate.of(2019, 12, 3),
            LocalTime.of(8, 0)
        )
        assertEquals(
            Document(
                emptyList(),
                listOf(
                    Day(
                        LocalDate.of(2019, 12, 3), listOf(
                        Line.OpenShift(LocalTime.of(8, 0))
                    ))
                )
            ),
            newDocument
        )
    }

    @Test
    internal fun `test that a blank line is inserted before inserted date`() {
        val timeTracker = createTimeTracker()
        val document = Document(
            emptyList(),
            listOf(
                Day(LocalDate.of(2019, 12, 2), listOf(
                    Line.ClosedShift(LocalTime.of(10, 0), LocalTime.of(10, 30))
                ))
            )
        )
        val newDocument = timeTracker.documentWithTrackingStarted(
            document,
            LocalDate.of(2019, 12, 3),
            LocalTime.of(8, 0)
        )
        assertEquals(
            Document(
                emptyList(),
                listOf(
                    Day(LocalDate.of(2019, 12, 2), listOf(
                        Line.ClosedShift(LocalTime.of(10, 0), LocalTime.of(10, 30)),
                        Line.Blank
                    )),
                    Day(LocalDate.of(2019, 12, 3), listOf(
                        Line.OpenShift(LocalTime.of(8, 0))
                    ))
                )
            ),
            newDocument
        )
    }

    @Test
    internal fun `test that we can start a shift on an already existing date`() {
        val timeTracker = createTimeTracker()
        val document = Document(
            emptyList(),
            listOf(
                Day(LocalDate.of(2019, 12, 2), listOf(
                    Line.ClosedShift(LocalTime.of(10, 0), LocalTime.of(10, 30))
                )),
                Day(LocalDate.of(2019, 12, 3), listOf(
                    Line.ClosedShift(LocalTime.of(11, 0), LocalTime.of(11, 40))
                ))
            )
        )
        val newDocument = timeTracker.documentWithTrackingStarted(
            document,
            LocalDate.of(2019, 12, 3),
            LocalTime.of(12, 0)
        )
        assertEquals(
            Document(
                emptyList(),
                listOf(
                    Day(LocalDate.of(2019, 12, 2), listOf(
                        Line.ClosedShift(LocalTime.of(10, 0), LocalTime.of(10, 30))
                    )),
                    Day(LocalDate.of(2019, 12, 3), listOf(
                        Line.ClosedShift(LocalTime.of(11, 0), LocalTime.of(11, 40)),
                        Line.OpenShift(LocalTime.of(12, 0))
                    ))
                )
            ),
            newDocument
        )
    }

    @Test
    internal fun `test that new open shifts are added right after last existing shift`() {
        val timeTracker = createTimeTracker()
        val document = Document(
            emptyList(),
            listOf(
                Day(LocalDate.of(2019, 12, 2), listOf(
                    Line.ClosedShift(LocalTime.of(10, 0), LocalTime.of(10, 30)),
                    Line.Blank
                ))
            )
        )
        val newDocument = timeTracker.documentWithTrackingStarted(
            document,
            LocalDate.of(2019, 12, 2),
            LocalTime.of(12, 0)
        )
        assertEquals(
            Document(
                emptyList(),
                listOf(
                    Day(LocalDate.of(2019, 12, 2), listOf(
                        Line.ClosedShift(LocalTime.of(10, 0), LocalTime.of(10, 30)),
                        Line.OpenShift(LocalTime.of(12, 0)),
                        Line.Blank
                    ))
                )
            ),
            newDocument
        )
    }

    @Test
    internal fun `tests that we can not start a shift if one is already started`() {
        val timeTracker = createTimeTracker()
        assertThrows<TrackerFileAlreadyHasOpenShiftException> {
            timeTracker.documentWithTrackingStarted(
                Document(
                    emptyList(),
                    listOf(
                        Day(
                            LocalDate.of(2020, 4, 20),
                            listOf(
                                Line.OpenShift(LocalTime.of(12, 0))
                            )
                        )
                    )
                ),
                LocalDate.of(2020, 4, 20),
                LocalTime.of(13, 0)
            )
        }
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