package com.puneeth.aiinterviewcoach.domain.model

data class MockInterviewSession(
    val id: Long,
    val startedAt: Long,
    val completedAt: Long,
    val totalQuestions: Int,
    val answeredCount: Int,
    val timeLimitSeconds: Int,
    val category: String?,
) {
    val skippedCount: Int get() = totalQuestions - answeredCount
    val scorePercent: Int get() = if (totalQuestions == 0) 0 else (answeredCount * 100) / totalQuestions
}
