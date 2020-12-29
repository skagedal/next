package tech.skagedal.assistant.git

class BranchNotAvailable: RuntimeException(
    "No branch like that was available"
)