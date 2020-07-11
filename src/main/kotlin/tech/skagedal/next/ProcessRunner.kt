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
        ProcessBuilder("brew", "upgrade")
            .inheritIO()
            .start()
            .waitFor()
    }

    private fun printCommand(command: List<String>) {
        println(command.joinToString(" "))
    }
}