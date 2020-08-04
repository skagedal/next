package tech.skagedal.assistant.configuration

import org.intellij.lang.annotations.Language
import tech.skagedal.assistant.general.matcherIfMatches
import java.util.regex.Matcher
import java.util.regex.Pattern

class WhenExpressionParser {
    data class ExpressionMatcher(
        @Language("RegExp")
        val pattern: String,
        val transform: (Matcher) -> WhenExpression
    ) {
        val regex = Pattern.compile(pattern)
    }

    private val expressionMatchers: List<ExpressionMatcher> = listOf(
        ExpressionMatcher("never") {
            WhenExpression.Never
        },
        ExpressionMatcher("always") {
            WhenExpression.Always
        },
        ExpressionMatcher("daily") {
            WhenExpression.EveryNDays(1)
        },
        ExpressionMatcher("^every (?<n>[0-9]+) days$") {
            WhenExpression.EveryNDays(it.group("n").toInt())
        }
    )

    fun parse(string: String) = expressionMatchers
        .asSequence()
        .map { matcher -> matcher.regex.matcherIfMatches(string)?.let { matcher.transform(it) } }
        .filterNotNull()
        .firstOrNull()
        ?: throw UnknownWhenString(
            "Can't parse $string as a \"when\" string",
            string
        )
}