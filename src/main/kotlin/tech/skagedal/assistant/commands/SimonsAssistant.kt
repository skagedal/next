package tech.skagedal.assistant.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class SimonsAssistant(
    private val subcommands: List<CliktCommand>
) : CliktCommand(
    name = "simons-assistant"
) {
    @PostConstruct
    fun init() {
        subcommands(subcommands)
    }

    override fun run() = Unit
}