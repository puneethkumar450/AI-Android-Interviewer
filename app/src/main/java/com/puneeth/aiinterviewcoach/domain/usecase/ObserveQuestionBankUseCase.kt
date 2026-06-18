package com.puneeth.aiinterviewcoach.domain.usecase

import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.repository.QuestionRepository
import javax.inject.Inject

class ObserveQuestionBankUseCase @Inject constructor(
    private val questionRepository: QuestionRepository,
) {
    operator fun invoke(
        searchQuery: String,
        category: InterviewCategory?,
        difficulty: InterviewDifficulty?,
        bookmarksOnly: Boolean,
    ) = questionRepository.observeQuestions(searchQuery, category, difficulty, bookmarksOnly)
}
