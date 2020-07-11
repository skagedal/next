package tech.skagedal.next.tasks

import com.google.api.client.json.jackson2.JacksonFactory
import tech.skagedal.next.ProcessRunner
import tech.skagedal.next.RunnableTask
import java.nio.file.FileSystem

class GmailCheckerTaskFactory(
    val fileSystem: FileSystem,
    val processRunner: ProcessRunner,
    val jacksonFactory: JacksonFactory
) {
    fun task(account: String): RunnableTask = GmailCheckerTask(fileSystem, processRunner, jacksonFactory, account)
}