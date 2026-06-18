package com.puneeth.aiinterviewcoach.domain.model

data class ProgressSummary(
    val totalQuestions: Int,
    val viewedQuestions: Int,
    val completedQuestions: Int,
    val currentStreak: Int,
    val bookmarksCount: Int,
    val continueQuestionId: Long?,
    val categoryProgress: List<CategoryProgress>,
    val difficultyProgress: List<DifficultyProgress>,
)
