package com.puneeth.aiinterviewcoach.domain.model

data class PracticeQuestion(
    val id: Long,
    val category: InterviewCategory,
    val difficulty: InterviewDifficulty,
    val question: String,
    val idealAnswer: String,
    val bookmarked: Boolean,
    val tags: List<String>,
)
