package tech.skagedal.assistant.git

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.io.File
import java.nio.file.Path

internal class GitRepoTest {
    @Test
    internal fun `test getting branches`() {
        val repo = testRepo("repo-with-some-branches")
        assertEquals(
            listOf("existing", "master"),
            repo.getBranches().map { it.refname }.sorted()
        )
    }

    @Test
    internal fun `test switching branches`() {
        val repo = testRepo("repo-with-some-branches")
        repo.checkoutFirstAvailableBranch(listOf("foo", "existing"))
        assertEquals(
            "existing",
            getCurrentBranch(repo.dir)
        )
    }

    private fun getCurrentBranch(dir: Path): String {
        val process = ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD")
            .directory(dir.toFile())
            .start()
        return String(process.getInputStream().readAllBytes()).trim()
    }


    private fun testRepo(repo: String): GitRepo {
        val tempDir = Files.createTempDirectory(repo)
        val file = File("src/test/resources/$repo.tar.gz")
        val absolutePath = file.absolutePath
        val output = ProcessBuilder("tar", "xzf", absolutePath)
            .directory(tempDir.toFile())
            .start()
            .waitFor()
        assertEquals(0, output)
        return GitRepo(tempDir.resolve(repo))
    }
}