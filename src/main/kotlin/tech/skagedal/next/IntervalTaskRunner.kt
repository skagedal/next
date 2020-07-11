package tech.skagedal.next

class IntervalTaskRunner(
    val processRunner: ProcessRunner
) {
    fun run() {
        processRunner.runBrewUpgrade()
    }
}