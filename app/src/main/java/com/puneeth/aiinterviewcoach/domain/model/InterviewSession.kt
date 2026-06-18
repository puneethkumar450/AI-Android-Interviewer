package com.puneeth.aiinterviewcoach.domain.model

data class InterviewSession(
    val id: Long,
    val category: InterviewCategory,
    val difficulty: InterviewDifficulty,
    val startedAt: Long,
    val completedAt: Long?,
    val messages: List<InterviewMessage>,
    val feedback: SessionFeedback?,
)
