package tech.skagedal.assistant.commands

import com.github.ajalt.clikt.core.CliktCommand
import org.springframework.stereotype.Component
import tech.skagedal.assistant.tracker.TimeTracker
import java.time.Duration
import java.time.LocalDate

@Component
class TrackReportCommand(
    private val timeTracker: TimeTracker
) : CliktCommand(name = "track-report") {
    override fun run() {
        val report = timeTracker.weekReportForDate(LocalDate.now())
        println("You have worked ${report.durationToday.format()} today.")
        println("You have worked ${report.durationThisWeek.format()} this week.")
    }

    fun Duration.format() = "${toHours().formatHours()} ${toMinutesPart().formatMinutes()}"
    fun Number.formatHours() = withUnit("hour", "hours")
    fun Number.formatMinutes() = withUnit("minute", "minutes")
}

fun Number.withUnit(singular: String, plural: String) =
    if (this.toInt() == 1)
        "$this $singular"
    else
        "$this $plural"
