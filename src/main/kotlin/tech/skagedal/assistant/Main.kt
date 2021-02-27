package tech.skagedal.assistant

import com.github.ajalt.clikt.core.subcommands
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import tech.skagedal.assistant.commands.GitCleanCommand
import tech.skagedal.assistant.commands.GitFetchCommand
import tech.skagedal.assistant.commands.GitReposCommand
import tech.skagedal.assistant.commands.NextCommand
import tech.skagedal.assistant.commands.SimonsAssistant
import tech.skagedal.assistant.commands.TrackEditCommand
import tech.skagedal.assistant.commands.TrackReportCommand
import tech.skagedal.assistant.commands.TrackStartCommand
import tech.skagedal.assistant.commands.TrackStopCommand

private object Main {
    val logger = LoggerFactory.getLogger(javaClass)

    fun createApplicationContext(): ApplicationContext =
        AnnotationConfigApplicationContext(Config::class.java).apply {
            scan(Main.javaClass.packageName)
            registerShutdownHook()
        }
}

inline fun <reified T> ApplicationContext.bean() : T = getBean(T::class.java)

fun main(args: Array<String>) {
    // Logging is configured in LoggingConfigurator, which logback finds because of the file
    // META-INF.services.ch.qos.logback.classic.spi.Configurator.

    Main.logger.info("Starting simons-assistant")

    val context = Main.createApplicationContext()

    // Commands

    val simonsAssistant = SimonsAssistant().subcommands(
        context.bean<NextCommand>(),
        context.bean<TrackEditCommand>(),
        context.bean<TrackReportCommand>(),
        context.bean<TrackStartCommand>(),
        context.bean<TrackStopCommand>(),
        context.bean<GitCleanCommand>(),
        context.bean<GitFetchCommand>(),
        context.bean<GitReposCommand>()
    )

    simonsAssistant.main(args)
}

