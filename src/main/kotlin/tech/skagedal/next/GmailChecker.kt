package tech.skagedal.next

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
import com.google.api.services.gmail.model.ListMessagesResponse
import com.google.api.services.gmail.model.ListThreadsResponse
import java.nio.file.FileSystem
import java.nio.file.Files

private const val CREDENTIALS_FILENAME = "google-oauth-credentials.json"
private const val TOKENS_DIRECTORY_PATHNAME = "tokens"

class GmailChecker(
    val fileSystem: FileSystem,
    val processRunner: ProcessRunner,
    val jacksonFactory: JacksonFactory
) {

    fun run() {
        println("Checking inbox...")
        val transport = GoogleNetHttpTransport.newTrustedTransport()
        val service = Gmail.Builder(transport, jacksonFactory, getCredentials(transport))
            .setApplicationName("Next Assistant")
            .build()

        val response = service
            .users()
            .threads()
            .list("me")
            .setQ("in:inbox")
            .execute()
        if (response.threads.isEmpty()) {
            println("Inbox is empty!")
        } else {
            println("Inbox is not empty (${response.resultSizeEstimate} messages).  Opening Gmail.")
            println("(To trouble shoot this, run next with NEXT_DEBUG_GMAIL=TRUE in the environment.)")
            if (System.getenv("NEXT_DEBUG_GMAIL") == "TRUE") {
                printAllMessages(response)
            }
            processRunner.openUrl("https://gmail.com")
        }
    }

    private fun printAllMessages(response: ListThreadsResponse) {
        for (m in response.threads) {
            println("id: ${m.id}")
            println(" - ${m.snippet}")
        }
    }

    private fun getCredentials(transport: NetHttpTransport): Credential {
        val credentialsPath = fileSystem.nextDirectory().resolve(CREDENTIALS_FILENAME)
        val tokensDirectoryPath = fileSystem.nextDataDirectory().resolve(TOKENS_DIRECTORY_PATHNAME)
        val clientSecrets = Files.newBufferedReader(credentialsPath).use { reader ->
            GoogleClientSecrets.load(jacksonFactory, reader)
        }

        val flow = GoogleAuthorizationCodeFlow.Builder(
            transport, jacksonFactory, clientSecrets, listOf(GmailScopes.GMAIL_READONLY)
        )
            .setDataStoreFactory(FileDataStoreFactory(tokensDirectoryPath.toFile()))
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }
}