package tech.skagedal.assistant.tasks

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.gmail.model.ListThreadsResponse
import org.slf4j.LoggerFactory
import tech.skagedal.assistant.ProcessRunner
import tech.skagedal.assistant.RunnableTask
import tech.skagedal.assistant.TaskResult
import tech.skagedal.assistant.assistantDataDirectory
import tech.skagedal.assistant.assistantDirectory
import java.nio.file.FileSystem
import java.nio.file.Files

private const val CREDENTIALS_FILENAME = "google-oauth-credentials.json"
private const val TOKENS_DIRECTORY_PATHNAME = "tokens"

class GmailCheckerTask(
    val fileSystem: FileSystem,
    val processRunner: ProcessRunner,
    val jacksonFactory: JacksonFactory,
    val account: String
) : RunnableTask {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun runTask(): TaskResult {
        logger.info("Checking account {}.", account)
        val transport = GoogleNetHttpTransport.newTrustedTransport()
        val service = Gmail.Builder(transport, jacksonFactory, getCredentials(transport, account))
            .setApplicationName("simons-assistant")
            .build()

        val response = service
            .users()
            .threads()
            .list("me")
            .setQ("in:inbox")
            .execute()

        if (response.resultSizeEstimate == 0L) {
            logger.info("Inbox of {} is empty.", account)
            return TaskResult.Proceed
        } else {
            logger.info("Inbox of {} contains {} messages. Opening Gmail.", account, response.resultSizeEstimate)
            traceAllMessages(response)
            processRunner.openUrl("https://mail.google.com/mail/u/$account")
            return TaskResult.ActionRequired
        }
    }

    private fun traceAllMessages(response: ListThreadsResponse) {
        for (m in response.threads) {
            logger.trace("id: ${m.id}")
            logger.trace(" - ${m.snippet}")
        }
    }

    private fun getCredentials(transport: NetHttpTransport, account: String): Credential {
        val credentialsPath = fileSystem.assistantDirectory().resolve(CREDENTIALS_FILENAME)
        val tokensDirectoryPath = fileSystem.assistantDataDirectory().resolve(TOKENS_DIRECTORY_PATHNAME)
        val clientSecrets = Files.newBufferedReader(credentialsPath).use { reader ->
            GoogleClientSecrets.load(jacksonFactory, reader)
        }

        val flow = GoogleAuthorizationCodeFlow.Builder(
            transport, jacksonFactory, clientSecrets, listOf(GmailScopes.GMAIL_READONLY)
        )
            .setDataStoreFactory(FileDataStoreFactory(tokensDirectoryPath.toFile()))
            .setAccessType("offline")
            .build()
        // It would be great if we could make it open up the Google login screen with the right account pre-selected,
        // but I can't figure out how to do that.
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize(account)
    }
}