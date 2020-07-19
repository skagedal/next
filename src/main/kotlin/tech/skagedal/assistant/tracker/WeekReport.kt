package tech.skagedal.assistant.tracker

import java.time.Duration

data class WeekReport(
    val durationToday: Duration,
    val durationThisWeek: Duration,
    val isOngoing: Boolean
)