package tech.skagedal.assistant.general

fun <T> ((T) -> Boolean).not() = { t: T -> !this(t) }

