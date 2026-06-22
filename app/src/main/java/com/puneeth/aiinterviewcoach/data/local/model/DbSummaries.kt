package com.puneeth.aiinterviewcoach.data.local.model

data class CategorySummaryRow(
    val category: String,
    val questionCount: Int,
)

data class DifficultySummaryRow(
    val dimension: String,
    val questionCount: Int,
)

data class ProgressSummaryRow(
    val dimension: String,
    val completedCount: Int,
    val totalCount: Int,
)

data class LastViewedRow(
    val category: String,
    val lastViewedAt: Long,
)

data class CategoryConfidenceRow(
    val category: String,
    val easyCount: Int,
    val okayCount: Int,
    val hardCount: Int,
)
