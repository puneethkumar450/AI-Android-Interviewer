package com.puneeth.aiinterviewcoach.domain.model

data class ResultAnalysis(
    val strengths: List<String>,
    val weaknesses: List<String>,
    val improvementSuggestions: List<String>,
    val recommendedLearningTopics: List<String>,
)
