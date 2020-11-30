package tech.skagedal.assistant

class ProcessExitedUnsuccessfullyException(val command: List<String>, val code: Int) : RuntimeException(
    "The command ${command} exited with status code ${code}."
)