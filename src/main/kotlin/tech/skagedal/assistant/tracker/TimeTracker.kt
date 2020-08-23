package tech.skagedal.assistant.tracker

import java.nio.file.Files
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class TrackerFileAlreadyHasOpenShiftException(override val message: String?) : RuntimeException(message)

class TimeTracker(
    private val repository: Repository,
    private val serializer: Serializer,
    private val standardWorkDayMinutes: Long
) {
    fun weekReportForDate(date: LocalDate): WeekReport {
        val path = repository.weekTrackerFileCreateIfNeeded(date)
        val document = Files.newBufferedReader(path).use { serializer.parseDocument(it) }
        val currentTime = LocalTime.now()

        return weekReportForDateInDocument(document, date, currentTime)
    }

    fun weekReportForDateInDocument(
        document: Document,
        date: LocalDate,
        currentTime: LocalTime
    ): WeekReport {
        return WeekReport(
            document.days.find { it.date == date }?.let { trackedDurationForDay(it, currentTime) } ?: Duration.ZERO,
            minutesForDocument(document, currentTime),
            document.hasOpenShift()
        )
    }

    fun minutesForDocument(document: Document, currentTime: LocalTime) =
        document.days.map { trackedDurationForDay(it, currentTime) }.sum()

    fun trackedDurationForDay(day: Day, currentTime: LocalTime) =
        day.lines.map { trackedDurationForLine(it, currentTime) }.sum()

    fun trackedDurationForLine(line: Line, currentTime: LocalTime) =
        when (line) {
            is Line.Comment -> Duration.ZERO
            is Line.DayHeader -> Duration.ZERO
            is Line.OpenShift -> Duration.between(line.startTime, currentTime)
            is Line.ClosedShift -> Duration.between(line.startTime, line.stopTime)
            is Line.SpecialDay -> Duration.of(standardWorkDayMinutes, ChronoUnit.MINUTES)
            is Line.SpecialShift -> Duration.between(line.startTime, line.stopTime)
            Line.Blank -> Duration.ZERO
        }

    fun startTracking(date: LocalDate, time: LocalTime) {
        val path = repository.weekTrackerFileCreateIfNeeded(date)
        val document = Files.newBufferedReader(path).use { serializer.parseDocument(it) }
        val newDocument = documentWithTrackingStarted(document, date, time)
        Files.newBufferedWriter(path).use { serializer.writeDocument(newDocument, it) }
    }

    fun documentWithTrackingStarted(document: Document, date: LocalDate, time: LocalTime): Document {
        if (document.days.any { it.lines.any { it is Line.OpenShift } }) {
            throw TrackerFileAlreadyHasOpenShiftException("There is already an open shift")
        }

        val day = document.days.find { it.date == date }
        return if (day != null) {
            document.copy(
                days = document.days.map {
                    if (it === day) {
                        it.copy(
                            lines = it.lines + listOf(Line.OpenShift(time))
                        )
                    } else {
                        it
                    }
                }
            )
        } else {
            document.copy(
                days =
                    document.days.takeWhile { it.date.isBefore(date) } +
                        listOf(Day(date, listOf(Line.OpenShift(time)))) +
                        document.days.dropWhile { it.date.isBefore(date) }
            )
        }
    }

    private fun Document.hasOpenShift() = days.any { it.hasOpenShift() }
    private fun Day.hasOpenShift() = lines.any { it is Line.OpenShift }

    private fun Iterable<Duration>.sum() = fold(Duration.ZERO, Duration::plus)
}

