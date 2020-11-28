package tech.skagedal.assistant

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.FileAppender
import com.google.api.client.json.jackson2.JacksonFactory
import org.slf4j.LoggerFactory
import tech.skagedal.assistant.commands.GitCleanCommand
import tech.skagedal.assistant.commands.Next
import tech.skagedal.assistant.commands.SimonsAssistant
import tech.skagedal.assistant.commands.TrackEdit
import tech.skagedal.assistant.commands.TrackReport
import tech.skagedal.assistant.commands.TrackStart
import tech.skagedal.assistant.commands.TrackStop
import tech.skagedal.assistant.configuration.ConfigurationLoader
import tech.skagedal.assistant.tasks.FileSystemLinterTaskFactory
import tech.skagedal.assistant.tasks.GitReposTaskFactory
import tech.skagedal.assistant.tasks.GmailCheckerTaskFactory
import tech.skagedal.assistant.tasks.IntervalTaskFactory
import tech.skagedal.assistant.tracker.Serializer
import tech.skagedal.assistant.tracker.TimeTracker
import tech.skagedal.assistant.ui.UserInterface
import java.nio.file.FileSystems

private object Main {
    val logger = LoggerFactory.getLogger(javaClass)
}

fun main(args: Array<String>) {
    // Logging is configured in LoggingConfigurator, which logback finds because of the file
    // META-INF.services.ch.qos.logback.classic.spi.Configurator.

    Main.logger.info("Starting simons-assistant")

    val processRunner = ProcessRunner()
    val fileSystem = FileSystems.getDefault()
    val repository = Repository(fileSystem)
    val configurationLoader = ConfigurationLoader()
    val userInterface = UserInterface()

    val fileSystemLinter = FileSystemLinterTaskFactory(
        fileSystem,
        processRunner
    )
    val intervalTaskRunner = IntervalTaskFactory(
        fileSystem,
        processRunner,
        repository
    )
    val gmailChecker = GmailCheckerTaskFactory(
        fileSystem,
        processRunner,
        JacksonFactory.getDefaultInstance()
    )
    val gitReposTaskFactory = GitReposTaskFactory(
        fileSystem
    )

    val nextCommand = Next(
        fileSystem,
        userInterface,
        repository,
        configurationLoader,
        fileSystemLinter,
        intervalTaskRunner,
        gmailChecker,
        gitReposTaskFactory
    )

    val trackerSerializer = Serializer()
    val trackerRepository = tech.skagedal.assistant.tracker.Repository(
        fileSystem,
        trackerSerializer
    )
    val timeTracker = TimeTracker(trackerRepository, trackerSerializer, 60 * 8)

    val trackEditCommand = TrackEdit(
        trackerRepository,
        processRunner
    )
    val trackReportCommand = TrackReport(timeTracker)
    val trackStartCommand = TrackStart(timeTracker)
    val trackStopCommand = TrackStop(timeTracker)

    val gitCleanCommand = GitCleanCommand(fileSystem, userInterface)

    val simonsAssistant = SimonsAssistant(
        listOf(nextCommand, trackEditCommand, trackReportCommand, trackStartCommand, trackStopCommand, gitCleanCommand)
    )

    simonsAssistant.main(args)
}