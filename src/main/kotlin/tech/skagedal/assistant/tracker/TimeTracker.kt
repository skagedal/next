package tech.skagedal.assistant.tracker

import java.time.LocalDate

class TimeTracker(
    private val repository: Repository,
    private val serializer: Serializer
) {
    fun weekReportForDate(date: LocalDate): WeekReport {
        return WeekReport(0, 0, false)
    }
}