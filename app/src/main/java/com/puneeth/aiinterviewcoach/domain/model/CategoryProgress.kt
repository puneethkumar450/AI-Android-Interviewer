package com.puneeth.aiinterviewcoach.domain.model

data class CategoryProgress(
    val category: InterviewCategory,
    val completedCount: Int,
    val totalCount: Int,
) {
    val completionPercent: Int
        get() = if (totalCount == 0) 0 else (completedCount * 100) / totalCount
}
