package com.puneeth.aiinterviewcoach.domain.usecase

import com.puneeth.aiinterviewcoach.domain.repository.QuestionRepository
import javax.inject.Inject

class ObserveHomeSummaryUseCase @Inject constructor(
    private val repository: QuestionRepository,
) {
    operator fun invoke() = repository.observeHomeSummary()
}
