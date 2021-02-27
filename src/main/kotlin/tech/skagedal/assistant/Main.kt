package tech.skagedal.assistant

import org.slf4j.LoggerFactory
import tech.skagedal.assistant.commands.SimonsAssistant
import tech.skagedal.assistant.ioc.bean
import tech.skagedal.assistant.ioc.createApplicationContext

private object Main {
    val logger = LoggerFactory.getLogger(javaClass)

    fun main(args: Array<String>) {
        logger.info("Starting simons-assistant")

        createApplicationContext(Main.javaClass)
            .bean<SimonsAssistant>()
            .main(args)
    }
}

fun main(args: Array<String>) = Main.main(args)

