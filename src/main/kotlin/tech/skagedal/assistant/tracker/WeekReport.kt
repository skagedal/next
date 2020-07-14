package tech.skagedal.assistant.tracker

data class WeekReport(
    val timeToday: Int,
    val timeThisWeek: Int,
    val isOngoing: Boolean
)