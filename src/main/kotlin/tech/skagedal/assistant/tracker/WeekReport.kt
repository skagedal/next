package tech.skagedal.assistant.tracker

data class WeekReport(
    val minutesToday: Long,
    val minutesThisWeek: Long,
    val isOngoing: Boolean
)