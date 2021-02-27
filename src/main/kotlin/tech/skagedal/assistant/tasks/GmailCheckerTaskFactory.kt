package tech.skagedal.assistant.tasks

import com.google.api.client.json.jackson2.JacksonFactory
import org.springframework.stereotype.Component
import tech.skagedal.assistant.ProcessRunner
import tech.skagedal.assistant.RunnableTask
import java.nio.file.FileSystem

@Component
class GmailCheckerTaskFactory(
    val fileSystem: FileSystem,
    val processRunner: ProcessRunner,
    val jacksonFactory: JacksonFactory
) {
    fun task(account: String): RunnableTask = GmailCheckerTask(fileSystem, processRunner, jacksonFactory, account)
}