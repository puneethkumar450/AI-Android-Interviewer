package com.puneeth.aiinterviewcoach.domain.model

data class CategoryConfidenceSummary(
    val category: InterviewCategory,
    val easyCount: Int,
    val okayCount: Int,
    val hardCount: Int,
) {
    val totalRated: Int get() = easyCount + okayCount + hardCount
}
