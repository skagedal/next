package tech.skagedal.assistant.commands

import com.github.ajalt.clikt.core.CliktCommand
import tech.skagedal.assistant.ProcessRunner
import tech.skagedal.assistant.tracker.Repository
import tech.skagedal.assistant.tracker.TimeTracker
import java.time.LocalDate

class TrackReport(
    private val timeTracker: TimeTracker
) : CliktCommand(name = "track-report") {
    override fun run() {
        println(timeTracker.weekReportForDate(LocalDate.now()))
    }
}