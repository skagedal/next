package tech.skagedal.assistant.configuration

object ProcessEnvironment {
    val DEBUG = System.getenv("DEBUG") == "true"
}