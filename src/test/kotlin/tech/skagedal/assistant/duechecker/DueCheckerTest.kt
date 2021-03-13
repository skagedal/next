package tech.skagedal.assistant.duechecker

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import tech.skagedal.assistant.configuration.WhenExpression
import java.time.LocalDate
import java.time.ZoneOffset

internal class DueCheckerTest {
    private val dueChecker = DueChecker()
    
    @Test
    internal fun daily_means_the_next_day() {
        val someDay = LocalDate.of(2007, 10, 3)
        val nextDay = someDay.plusDays(1)

        val notDue = dueChecker.check(
            WhenExpression.EveryNDays(1),
            instantWhenDone = someDay.atTime(10, 0).toInstant(ZoneOffset.UTC),
            instantNow = someDay.atTime(11, 0).toInstant(ZoneOffset.UTC)
        )
        assertFalse(notDue.isDue)

        val due = dueChecker.check(
            WhenExpression.EveryNDays(1),
            instantWhenDone = someDay.atTime(10, 0).toInstant(ZoneOffset.UTC),
            instantNow = nextDay.atTime(11, 0).toInstant(ZoneOffset.UTC)
        )
        assertTrue(due.isDue)

        val alsoDue = dueChecker.check(
            WhenExpression.EveryNDays(1),
            instantWhenDone = someDay.atTime(10, 0).toInstant(ZoneOffset.UTC),
            instantNow = nextDay.atTime(9, 0).toInstant(ZoneOffset.UTC)
        )
        assertTrue(alsoDue.isDue)
    }
}