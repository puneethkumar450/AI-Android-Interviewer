package com.puneeth.aiinterviewcoach.domain.usecase

import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.repository.AiInterviewRepository
import javax.inject.Inject

class GenerateMockReplyUseCase @Inject constructor(
    private val repository: AiInterviewRepository,
) {
    suspend operator fun invoke(
        category: InterviewCategory,
        difficulty: InterviewDifficulty,
        currentQuestion: String,
        answer: String,
    ) = repository.continueInterview(category, difficulty, currentQuestion, answer)
}
