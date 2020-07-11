package tech.skagedal.next

import java.nio.file.Path

class ProcessRunner {
    fun runInteractiveShell(directory: Path) {
        ProcessBuilder("zsh")
            .directory(directory.toFile())
            .inheritIO()
            .start()
            .waitFor()
    }

    fun runBrewUpgrade() {
        val command = listOf("brew", "upgrade")
        printCommand(command)
        ProcessBuilder(command)
            .inheritIO()
            .start()
            .waitFor()
    }

    private fun printCommand(command: List<String>) {
        println(command.joinToString(" "))
    }
}