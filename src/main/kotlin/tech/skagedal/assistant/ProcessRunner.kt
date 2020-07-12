package tech.skagedal.assistant

import java.nio.file.Path

class ProcessRunner {
    fun runInteractiveShell(directory: Path) {
        runCommand(listOf("zsh"), directory)
    }

    fun runBrewUpgrade() {
        runCommand(listOf("brew", "upgrade"))
    }

    fun openUrl(url: String) {
        runCommand(listOf("open", url))
    }

    private fun runCommand(command: List<String>, directory: Path? = null) {
        printCommand(command)
        ProcessBuilder(command)
            .apply { if (directory != null) directory(directory.toFile()) }
            .inheritIO()
            .start()
            .waitFor()
    }

    private fun printCommand(command: List<String>) {
        println(command.joinToString(" "))
    }

    fun runEditor(path: Path) {
        runCommand(
            listOf(
                System.getenv("EDITOR"),
                path.toString()
            )
        )
    }
}