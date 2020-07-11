package tech.skagedal.assistant.tracker

import tech.skagedal.assistant.assistantDataDirectory
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields

sealed class Line {
    data class Comment(val text: String) : Line()
    data class DayHeader(val date: LocalDate) : Line()
    data class OpenShift(val startTime: LocalTime) : Line()
    data class ClosedShift(val startTime: LocalTime, val stopTime: LocalTime) : Line()
    data class SpecialDay(val text: String) : Line()
    data class SpecialShift(val text: String, val startTime: LocalTime, val stopTime: LocalTime) : Line()
    object Blank : Line()
}

data class Document(
    val lines: List<Line>
)

class Repository(
    val fileSystem: FileSystem
) {
    private val formatter = DateTimeFormatter.ofPattern("Y-'W'ww")

    fun weekTrackerFileCreateIfNeeded(date: LocalDate): Path {
        val path = pathForWeekTrackerFile(date)
        Files.newBufferedWriter(path, StandardOpenOption.CREATE_NEW).use { writer ->
            writer.write("")
        }
        return path
    }

    fun pathForWeekTrackerFile(date: LocalDate) =
        fileSystem.assistantDataDirectory().resolve("tracker").resolve(date.format(formatter) + ".txt")

    fun defaultDocument(date: LocalDate): Document {
        val lines = (1..5).flatMap { dayNumber ->
            listOf(
                Line.DayHeader(date.with(WeekFields.ISO.dayOfWeek(), dayNumber.toLong())),
                Line.Blank
            )
        }
        return Document(lines)
    }
}