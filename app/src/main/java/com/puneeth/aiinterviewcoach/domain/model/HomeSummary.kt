package com.puneeth.aiinterviewcoach.domain.model

data class HomeSummary(
    val totalQuestions: Int,
    val bookmarksCount: Int,
    val continueQuestionId: Long?,
    val categories: List<CategorySummary>,
    val difficultySummaries: List<HomeDifficultySummary>,
)
