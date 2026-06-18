package com.puneeth.aiinterviewcoach.domain.usecase

import com.puneeth.aiinterviewcoach.domain.repository.ProgressRepository
import javax.inject.Inject

class TrackQuestionViewedUseCase @Inject constructor(
    private val repository: ProgressRepository,
) {
    suspend operator fun invoke(questionId: Long) {
        repository.markQuestionViewed(questionId)
        repository.saveLastOpenedQuestion(questionId)
    }
}
