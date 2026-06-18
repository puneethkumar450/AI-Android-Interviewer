package com.puneeth.aiinterviewcoach.domain.model

data class ProgressSummary(
    val completedInterviews: Int,
    val currentStreak: Int,
    val averageScore: Int,
    val categoryScores: Map<InterviewCategory, Int>,
    val weakAreas: List<String>,
)
