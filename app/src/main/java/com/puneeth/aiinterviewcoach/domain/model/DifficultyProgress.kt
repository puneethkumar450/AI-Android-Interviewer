package com.puneeth.aiinterviewcoach.domain.model

data class DifficultyProgress(
    val difficulty: InterviewDifficulty,
    val completedCount: Int,
    val totalCount: Int,
) {
    val completionPercent: Int
        get() = if (totalCount == 0) 0 else (completedCount * 100) / totalCount
}
