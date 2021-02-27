package tech.skagedal.assistant.tasks

import org.springframework.stereotype.Component
import tech.skagedal.assistant.ProcessRunner
import tech.skagedal.assistant.RunnableTask
import tech.skagedal.assistant.desktop
import tech.skagedal.assistant.downloads
import tech.skagedal.assistant.home
import tech.skagedal.assistant.isGloballyIgnored
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path

@Component
class FileSystemLinterTaskFactory(
    val fileSystem: FileSystem,
    val processRunner: ProcessRunner
) {
    fun standardTasks(): List<RunnableTask> = listOf(
        // Rule: We should not have non-hidden, non-directory files laying around in the home directory.

        FileSystemLinterTask(
            fileSystem.home(),
            "in your home directory",
            ::homeRules
        ),

        // Rule: We should not have files or directories laying around on the Desktop or in Downloads.  The .DS_Store file is ok.

        FileSystemLinterTask(
            fileSystem.desktop(),
            "on the Desktop",
            ::desktopRules
        ),
        FileSystemLinterTask(
            fileSystem.downloads(),
            "in the Downloads folder",
            ::desktopRules
        )
    )

    private fun homeRules(path: Path) =
        Files.isRegularFile(path) && !path.isGloballyIgnored() && !Files.isHidden(path)

    private fun desktopRules(path: Path) =
        !path.isGloballyIgnored()
}
