package com.puneeth.aiinterviewcoach.domain.repository

import com.puneeth.aiinterviewcoach.domain.model.ProgressSummary
import com.puneeth.aiinterviewcoach.domain.model.RecentActivity
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun observeProgressSummary(): Flow<ProgressSummary>
    fun observeLastOpenedQuestionId(): Flow<Long?>
    fun observeRecentActivity(): Flow<RecentActivity?>
    fun observeUnviewedCount(): Flow<Int>
    suspend fun markQuestionViewed(questionId: Long)
    suspend fun markQuestionCompleted(questionId: Long)
    suspend fun saveLastOpenedQuestion(questionId: Long)
    suspend fun resetProgress()
}
