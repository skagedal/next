package tech.skagedal.assistant

import java.lang.RuntimeException

class AssistantFatalException(override val message: String?): RuntimeException(message) {
}