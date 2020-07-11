package tech.skagedal.assistant

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.nio.file.FileSystems

internal class FileSystemHelpersKtTest {
    @Test
    internal fun `expand tilde`() {
        System.setProperty("user.home", "/home/the-user")
        val fileSystem = FileSystems.getDefault()
        val expanded = fileSystem.pathWithShellExpansions("~/the-directory")
        assertEquals(
            "/home/the-user/the-directory",
            expanded.toString()
        )
    }
}