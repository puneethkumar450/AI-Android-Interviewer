package com.puneeth.aiinterviewcoach.domain.model

data class PracticeQuestion(
    val id: Long,
    val category: InterviewCategory,
    val difficulty: InterviewDifficulty,
    val question: String,
    val answer: String,
    val explanation: String,
    val tags: List<String>,
    val isBookmarked: Boolean,
)
