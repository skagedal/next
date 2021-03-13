package tech.skagedal.assistant.duechecker

import tech.skagedal.assistant.configuration.WhenExpression
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class DueChecker {
    fun check(instantWhenDone: Instant?, instantNow: Instant, whenExpression: WhenExpression) =
        if (instantWhenDone == null) {
            Due(true, "it has never been done before")
        } else when(whenExpression) {
            WhenExpression.Never -> Due(false, "it should never be done")
            WhenExpression.Always -> Due(true, "it should always be done")
            is WhenExpression.EveryNDays -> {
                val then = LocalDateTime.ofInstant(instantWhenDone, ZoneOffset.UTC)
                val now = LocalDateTime.ofInstant(instantNow, ZoneOffset.UTC)
                val diff = then.until(now, ChronoUnit.DAYS)
                if (diff >= whenExpression.n) {
                    Due(true, "${whenExpression.n} or more days have passed")
                } else if (diff == 0L) {
                    Due(false, "it has been done today already")
                } else {
                    Due(false, "only $diff days have passed")
                }
            }
        }
}
