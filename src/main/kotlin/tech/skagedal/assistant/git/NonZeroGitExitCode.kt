package tech.skagedal.assistant.git

class NonZeroGitExitCode(exitCode: Int, output: String) : RuntimeException(
    "Unexpected git exit code: ${exitCode}\n${output}"
)