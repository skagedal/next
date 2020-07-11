package tech.skagedal.assistant.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class SimonsAssistant: CliktCommand {
    constructor(subcommands: List<CliktCommand>) {
        subcommands(subcommands)
    }

    override fun run() = Unit
}