package tech.skagedal.assistant.commands

import com.github.ajalt.clikt.core.CliktCommand
import tech.skagedal.assistant.ProcessRunner
import tech.skagedal.assistant.tracker.Repository
import tech.skagedal.assistant.tracker.Serializer
import java.time.LocalDate

class TrackEdit(
    val repository: Repository,
    val processRunner: ProcessRunner
) : CliktCommand(name = "track-edit") {
    override fun run() {
        val path = repository.weekTrackerFileCreateIfNeeded(LocalDate.now())
        processRunner.runEditor(path)
    }
}