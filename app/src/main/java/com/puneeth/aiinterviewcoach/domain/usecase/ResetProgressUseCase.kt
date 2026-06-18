package com.puneeth.aiinterviewcoach.domain.usecase

import com.puneeth.aiinterviewcoach.domain.repository.ProgressRepository
import javax.inject.Inject

class ResetProgressUseCase @Inject constructor(
    private val repository: ProgressRepository,
) {
    suspend operator fun invoke() = repository.resetProgress()
}
