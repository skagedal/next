package tech.skagedal.next

import java.nio.file.FileSystem
import java.nio.file.Path

fun FileSystem.home() = getPath(System.getProperty("user.home"))
fun FileSystem.desktop() = home().resolve("Desktop")

fun FileSystem.nextDirectory() = home().resolve(".next-assistant")
fun FileSystem.nextDataDirectory() = nextDirectory().resolve("data")
