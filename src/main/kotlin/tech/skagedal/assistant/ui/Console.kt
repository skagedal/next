package tech.skagedal.assistant.ui

object Console {
    fun newLine(count: Int = 1) = "\n".repeat(count)
    fun beginningOfLine() = "\r"
    fun moveUp(count: Int = 1) = "\u001b[${count}A"
    fun clearRestOfLine() = "\u001b[K"
}