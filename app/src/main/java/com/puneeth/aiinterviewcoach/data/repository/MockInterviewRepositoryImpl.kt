package com.puneeth.aiinterviewcoach.data.repository

import com.puneeth.aiinterviewcoach.data.local.dao.MockInterviewDao
import com.puneeth.aiinterviewcoach.data.local.entity.MockInterviewSessionEntity
import com.puneeth.aiinterviewcoach.domain.model.MockInterviewSession
import com.puneeth.aiinterviewcoach.domain.repository.MockInterviewRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class MockInterviewRepositoryImpl @Inject constructor(
    private val dao: MockInterviewDao,
) : MockInterviewRepository {

    override suspend fun startSession(
        totalQuestions: Int,
        timeLimitSeconds: Int,
        category: String?,
    ): Long = dao.insert(
        MockInterviewSessionEntity(
            startedAt = System.currentTimeMillis(),
            totalQuestions = totalQuestions,
            timeLimitSeconds = timeLimitSeconds,
            category = category,
        ),
    )

    override suspend fun completeSession(sessionId: Long, answeredCount: Int) {
        dao.complete(sessionId, answeredCount, System.currentTimeMillis())
    }

    override fun observeLatestSession(): Flow<MockInterviewSession?> =
        dao.observeLatestSession().map { it?.toDomain() }

    private fun MockInterviewSessionEntity.toDomain() = MockInterviewSession(
        id = id,
        startedAt = startedAt,
        completedAt = completedAt ?: startedAt,
        totalQuestions = totalQuestions,
        answeredCount = answeredCount,
        timeLimitSeconds = timeLimitSeconds,
        category = category,
    )
}
