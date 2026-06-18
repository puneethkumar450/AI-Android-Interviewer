package com.puneeth.aiinterviewcoach.domain.usecase

import com.puneeth.aiinterviewcoach.domain.repository.ProgressRepository
import javax.inject.Inject

class GetProgressSummaryUseCase @Inject constructor(
    private val repository: ProgressRepository,
) {
    operator fun invoke() = repository.observeProgressSummary()
}
