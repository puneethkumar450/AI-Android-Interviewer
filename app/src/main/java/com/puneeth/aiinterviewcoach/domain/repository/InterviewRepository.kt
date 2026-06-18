package com.puneeth.aiinterviewcoach.domain.repository

import com.puneeth.aiinterviewcoach.domain.model.InterviewSession
import kotlinx.coroutines.flow.Flow

interface InterviewRepository {
    fun observeCompletedSessions(): Flow<List<InterviewSession>>
    suspend fun saveInterviewSession(session: InterviewSession)
}
