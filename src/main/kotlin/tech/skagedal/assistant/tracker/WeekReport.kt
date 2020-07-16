package tech.skagedal.assistant.tracker

import java.time.Duration

data class WeekReport(
    val minutesToday: Duration,
    val minutesThisWeek: Duration,
    val isOngoing: Boolean
)