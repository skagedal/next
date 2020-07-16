package tech.skagedal.assistant.tracker

import java.nio.file.Files
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

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

    private fun Document.hasOpenShift() = days.any { it.hasOpenShift() }
    private fun Day.hasOpenShift() = lines.any { it is Line.OpenShift }

    private fun Iterable<Duration>.sum() = fold(Duration.ZERO, Duration::plus)
}

