package tech.skagedal.assistant.tracker

import java.io.Reader
import java.io.Writer
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.ResolverStyle
import java.time.format.TextStyle
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.Locale

class Serializer {
    private val formatter = DateTimeFormatterBuilder()
        .appendValue(ChronoField.HOUR_OF_DAY, 2)
        .appendLiteral(':')
        .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
        .toFormatter()

    fun writeDocument(document: Document, writer: Writer) {
        for (line in document.lines) {
            when (line) {
                is Line.Comment -> writer.write("# ${line.text}\n")
                is Line.DayHeader -> writer.write("[${headerDateFormat(line.date)}]\n")
                is Line.OpenShift -> writer.write("* ${formatTime(line.startTime)}-\n")
                is Line.ClosedShift -> writer.write("* ${formatTime(line.startTime)}-${formatTime(line.stopTime)}\n")
                is Line.SpecialDay -> writer.write("* ${line.text}\n")
                is Line.SpecialShift -> writer.write("* ${line.text} ${formatTime(line.startTime)}-${formatTime(line.stopTime)}\n")
                Line.Blank -> writer.write("\n")
            }
        }
    }

    fun parseDocument(reader: Reader): Document {
        return Document(listOf())
    }

    private fun headerDateFormat(date: LocalDate): String {
        val weekDay = date.dayOfWeek.getDisplayName(TextStyle.FULL_STANDALONE, Locale.ENGLISH).toLowerCase()
        val isoDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return "$weekDay $isoDate"
    }

    private fun formatTime(time: LocalTime) =
        time.truncatedTo(ChronoUnit.MINUTES).format(formatter)
}