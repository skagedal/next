package tech.skagedal.assistant.configuration

import com.fasterxml.jackson.databind.exc.ValueInstantiationException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class WhenExpressionParserTest {
    private val parser = WhenExpressionParser()

    @Test
    internal fun `when expressions are parsed correctly`() {
        assertEquals(WhenExpression.Always, parser.parse("always"))
        assertEquals(WhenExpression.Never, parser.parse("never"))
        assertEquals(WhenExpression.EveryNDays(1), parser.parse("daily"))
        assertEquals(WhenExpression.EveryNDays(5), parser.parse("every 5 days"))
        org.junit.jupiter.api.assertThrows<UnknownWhenString> { parser.parse("asdf") }
    }
}