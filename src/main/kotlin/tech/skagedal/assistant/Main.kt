package tech.skagedal.assistant

import com.google.api.client.json.jackson2.JacksonFactory
import org.slf4j.LoggerFactory
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
import java.nio.file.FileSystems

private object Main {
    val logger = LoggerFactory.getLogger(javaClass)

    fun configureLogging() {
        // https://stackoverflow.com/questions/16910955/programmatically-configure-logback-appender
        // http://logback.qos.ch/manual/appenders.html
    }

    fun testLogging() {
        Main.logger.info("Starting simons-assistant")
        Main.logger.warn("This is a warning")
        Main.logger.error("This is an error")
        Main.logger.debug("And this is debug")
    }
}

fun main(args: Array<String>) {
    Main.configureLogging()

    val processRunner = ProcessRunner()
    val fileSystem = FileSystems.getDefault()
    val repository = Repository(fileSystem)
    val configurationLoader = ConfigurationLoader()

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

    val simonsAssistant = SimonsAssistant(
        listOf(nextCommand, trackEditCommand, trackReportCommand, trackStartCommand, trackStopCommand)
    )

    simonsAssistant.main(args)
}