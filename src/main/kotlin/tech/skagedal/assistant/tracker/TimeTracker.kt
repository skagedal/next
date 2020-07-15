package tech.skagedal.assistant.tracker

import java.nio.file.Files
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
            document.days.find { it.date == date }?.let { minutesForDay(it, currentTime) } ?: 0,
            minutesForDocument(document, currentTime),
            document.hasOpenShift()
        )
    }

    fun minutesForDocument(document: Document, currentTime: LocalTime) =
        document.days.map { minutesForDay(it, currentTime) }.sum()

    fun minutesForDay(day: Day, currentTime: LocalTime) =
        day.lines.map { minutesForLine(it, currentTime) }.sum()

    fun minutesForLine(line: Line, currentTime: LocalTime) =
        when (line) {
            is Line.Comment -> 0
            is Line.DayHeader -> 0
            is Line.OpenShift -> line.startTime.until(currentTime, ChronoUnit.MINUTES)
            is Line.ClosedShift -> line.startTime.until(line.stopTime, ChronoUnit.MINUTES)
            is Line.SpecialDay -> standardWorkDayMinutes
            is Line.SpecialShift -> line.startTime.until(line.stopTime, ChronoUnit.MINUTES)
            Line.Blank -> 0
        }

    private fun Document.hasOpenShift() = days.any { it.hasOpenShift() }
    private fun Day.hasOpenShift() = lines.any { it is Line.OpenShift }
}

