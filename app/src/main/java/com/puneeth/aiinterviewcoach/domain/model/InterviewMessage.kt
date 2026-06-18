package com.puneeth.aiinterviewcoach.domain.model

data class InterviewMessage(
    val id: String,
    val role: MessageRole,
    val content: String,
    val timestamp: Long,
)

enum class MessageRole {
    INTERVIEWER,
    CANDIDATE,
    SYSTEM,
}
