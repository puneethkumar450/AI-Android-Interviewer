package com.puneeth.aiinterviewcoach.domain.model

data class MockInterviewTurn(
    val candidateAnswer: String,
    val nextQuestion: String,
    val quickFeedback: String,
    val provisionalScore: Int,
)
