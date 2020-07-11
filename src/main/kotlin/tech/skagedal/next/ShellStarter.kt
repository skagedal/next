package tech.skagedal.next

import java.nio.file.Path

class ShellStarter {
    fun start(directory: Path) {
        ProcessBuilder("zsh")
            .directory(directory.toFile())
            .inheritIO()
            .start()
            .waitFor()
    }
}