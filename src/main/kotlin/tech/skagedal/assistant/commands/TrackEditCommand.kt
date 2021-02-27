package tech.skagedal.assistant.commands

import com.github.ajalt.clikt.core.CliktCommand
import org.springframework.stereotype.Component
import tech.skagedal.assistant.ProcessRunner
import tech.skagedal.assistant.ioc.Subcommand
import tech.skagedal.assistant.tracker.TrackerRepository
import java.time.LocalDate

@Subcommand
class TrackEditCommand(
    val trackerRepository: TrackerRepository,
    val processRunner: ProcessRunner
) : CliktCommand(name = "track-edit") {
    override fun run() {
        val path = trackerRepository.weekTrackerFileCreateIfNeeded(LocalDate.now())
        processRunner.runEditor(path)
    }
}