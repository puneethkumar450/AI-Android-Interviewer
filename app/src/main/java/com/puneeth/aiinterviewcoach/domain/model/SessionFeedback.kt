package com.puneeth.aiinterviewcoach.domain.model

data class SessionFeedback(
    val score: Int,
    val strengths: List<String>,
    val weaknesses: List<String>,
    val improvementSuggestions: List<String>,
    val recommendedTopics: List<String>,
    val summary: String,
)
