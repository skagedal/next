package tech.skagedal.assistant.git

enum class UpstreamStatus {
    IDENTICAL,
    UPSTREAM_IS_AHEAD_OF_LOCAL,
    LOCAL_IS_AHEAD_OF_UPSTREAM,
    MERGE_NEEDED,
    UPSTREAM_IS_GONE
}

data class Upstream(
    val name: String,
    val status: UpstreamStatus
)

data class Branch(
    val refname: String,
    val upstream: Upstream?
)