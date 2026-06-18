package com.puneeth.aiinterviewcoach.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.puneeth.aiinterviewcoach.data.local.entity.InterviewSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InterviewSessionDao {
    @Query("SELECT * FROM interview_sessions ORDER BY completedAt DESC")
    fun observeCompletedSessions(): Flow<List<InterviewSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: InterviewSessionEntity)
}
