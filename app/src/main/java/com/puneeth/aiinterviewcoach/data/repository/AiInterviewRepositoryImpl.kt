package com.puneeth.aiinterviewcoach.data.repository

import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.model.MockInterviewTurn
import com.puneeth.aiinterviewcoach.domain.model.SessionFeedback
import com.puneeth.aiinterviewcoach.domain.repository.AiInterviewRepository
import javax.inject.Inject

class AiInterviewRepositoryImpl @Inject constructor(
) : AiInterviewRepository {
    override suspend fun startInterview(
        category: InterviewCategory,
        difficulty: InterviewDifficulty,
    ): String {
        return "Offline mode does not provide AI interviews in this release."
    }

    override suspend fun continueInterview(
        category: InterviewCategory,
        difficulty: InterviewDifficulty,
        currentQuestion: String,
        userAnswer: String,
    ): MockInterviewTurn {
        return MockInterviewTurn(
            candidateAnswer = userAnswer,
            nextQuestion = "Review the next saved question in the local pack.",
            quickFeedback = "AI follow-up is disabled in offline mode.",
            provisionalScore = 0,
        )
    }

    override suspend fun evaluateInterview(
        transcript: String,
        category: InterviewCategory,
        difficulty: InterviewDifficulty,
    ): SessionFeedback {
        return SessionFeedback(
            score = 0,
            strengths = emptyList(),
            weaknesses = emptyList(),
            improvementSuggestions = listOf("Offline mode focuses on local question practice only."),
            recommendedTopics = listOf(category.title),
            summary = "AI evaluation is not available in the offline release.",
        )
    }
}
