package tech.skagedal.next

import java.lang.RuntimeException

class NextFatalException(override val message: String?): RuntimeException(message) {
}