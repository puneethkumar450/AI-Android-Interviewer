package com.puneeth.aiinterviewcoach.domain.model

data class ResultAnalysis(
    val strengths: List<String> = emptyList(),
    val weaknesses: List<String> = emptyList(),
    val improvementSuggestions: List<String> = emptyList(),
    val recommendedLearningTopics: List<String> = emptyList(),
)
