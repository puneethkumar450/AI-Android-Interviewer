package com.puneeth.aiinterviewcoach.data.repository

import com.puneeth.aiinterviewcoach.data.local.dao.InterviewSessionDao
import com.puneeth.aiinterviewcoach.data.local.mapper.toDomain
import com.puneeth.aiinterviewcoach.data.local.mapper.toEntity
import com.puneeth.aiinterviewcoach.domain.model.InterviewSession
import com.puneeth.aiinterviewcoach.domain.repository.InterviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InterviewRepositoryImpl @Inject constructor(
    private val dao: InterviewSessionDao,
) : InterviewRepository {
    override fun observeCompletedSessions(): Flow<List<InterviewSession>> {
        return dao.observeCompletedSessions().map { sessions -> sessions.map { it.toDomain() } }
    }

    override suspend fun saveInterviewSession(session: InterviewSession) {
        dao.insert(session.toEntity())
    }
}
