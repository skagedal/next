package tech.skagedal.next

class ShellStarter {
    fun start() {
        ProcessBuilder("zsh")
            .inheritIO()
            .start()
            .waitFor()
    }
}