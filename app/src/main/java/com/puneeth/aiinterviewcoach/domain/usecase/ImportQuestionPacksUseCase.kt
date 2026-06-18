package com.puneeth.aiinterviewcoach.domain.usecase

import com.puneeth.aiinterviewcoach.domain.repository.QuestionRepository
import javax.inject.Inject

class ImportQuestionPacksUseCase @Inject constructor(
    private val repository: QuestionRepository,
) {
    suspend operator fun invoke() = repository.importQuestionPacksIfNeeded()
}
