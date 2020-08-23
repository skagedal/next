package tech.skagedal.assistant.commands

import com.github.ajalt.clikt.core.CliktCommand
import tech.skagedal.assistant.tracker.TimeTracker
import java.time.LocalDate
import java.time.LocalTime

class TrackStart(val timeTracker: TimeTracker) : CliktCommand(name = "track-start") {
    override fun run() {
        val date = LocalDate.now()
        val time = LocalTime.now()
        timeTracker.startTracking(date, time)
        println("Started tracking")
    }
}
