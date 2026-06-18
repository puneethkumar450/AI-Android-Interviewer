package com.puneeth.aiinterviewcoach.domain.repository

import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.model.MockInterviewTurn
import com.puneeth.aiinterviewcoach.domain.model.SessionFeedback

interface AiInterviewRepository {
    suspend fun startInterview(
        category: InterviewCategory,
        difficulty: InterviewDifficulty,
    ): String

    suspend fun continueInterview(
        category: InterviewCategory,
        difficulty: InterviewDifficulty,
        currentQuestion: String,
        userAnswer: String,
    ): MockInterviewTurn

    suspend fun evaluateInterview(
        transcript: String,
        category: InterviewCategory,
        difficulty: InterviewDifficulty,
    ): SessionFeedback
}
