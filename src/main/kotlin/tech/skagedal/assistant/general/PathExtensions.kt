package tech.skagedal.assistant.general

import java.nio.file.Files
import java.nio.file.Path

fun Path.filesInDirectory(): List<Path> = Files.newDirectoryStream(this).use { it.toList() }
