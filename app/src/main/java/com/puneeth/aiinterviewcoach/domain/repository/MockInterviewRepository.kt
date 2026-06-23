package com.puneeth.aiinterviewcoach.domain.repository

import com.puneeth.aiinterviewcoach.domain.model.MockInterviewSession
import kotlinx.coroutines.flow.Flow

interface MockInterviewRepository {
    suspend fun startSession(
        totalQuestions: Int,
        timeLimitSeconds: Int,
        category: String?,
    ): Long

    suspend fun completeSession(sessionId: Long, answeredCount: Int)

    fun observeLatestSession(): Flow<MockInterviewSession?>
}
