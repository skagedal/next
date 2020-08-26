package tech.skagedal.assistant.commands

import com.github.ajalt.clikt.core.CliktCommand
import tech.skagedal.assistant.tracker.TimeTracker
import java.time.LocalDate
import java.time.LocalTime

class TrackStop(val timeTracker: TimeTracker) : CliktCommand(name = "track-stop") {
    override fun run() {
        val date = LocalDate.now()
        val time = LocalTime.now()
        timeTracker.stopTracking(date, time)
        println("Stopped tracking")
    }
}