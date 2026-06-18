package com.puneeth.aiinterviewcoach.domain.usecase

import com.puneeth.aiinterviewcoach.domain.model.InterviewSession
import com.puneeth.aiinterviewcoach.domain.repository.InterviewRepository
import javax.inject.Inject

class SaveInterviewResultUseCase @Inject constructor(
    private val interviewRepository: InterviewRepository,
) {
    suspend operator fun invoke(session: InterviewSession) {
        interviewRepository.saveInterviewSession(session)
    }
}
