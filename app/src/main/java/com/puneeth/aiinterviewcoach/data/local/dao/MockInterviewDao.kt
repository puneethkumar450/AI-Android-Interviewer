package com.puneeth.aiinterviewcoach.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.puneeth.aiinterviewcoach.data.local.entity.MockInterviewSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MockInterviewDao {
    @Insert
    suspend fun insert(session: MockInterviewSessionEntity): Long

    @Query(
        "UPDATE mock_interview_sessions SET answeredCount = :answered, completedAt = :completedAt WHERE id = :id",
    )
    suspend fun complete(id: Long, answered: Int, completedAt: Long)

    @Query(
        "SELECT * FROM mock_interview_sessions WHERE completedAt IS NOT NULL ORDER BY startedAt DESC LIMIT 1",
    )
    fun observeLatestSession(): Flow<MockInterviewSessionEntity?>

    @Query(
        "SELECT * FROM mock_interview_sessions WHERE completedAt IS NOT NULL ORDER BY startedAt DESC LIMIT 5",
    )
    fun observeRecentSessions(): Flow<List<MockInterviewSessionEntity>>
}
