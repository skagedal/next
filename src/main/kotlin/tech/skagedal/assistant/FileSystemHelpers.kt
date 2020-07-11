package tech.skagedal.assistant

import java.nio.file.FileSystem

fun FileSystem.home() = getPath(System.getProperty("user.home"))
fun FileSystem.desktop() = home().resolve("Desktop")

fun FileSystem.assistantDirectory() = home().resolve(".simons-assistant")
fun FileSystem.tasksYmlFile() = assistantDirectory().resolve("tasks.yml")
fun FileSystem.assistantDataDirectory() = assistantDirectory().resolve("data")
