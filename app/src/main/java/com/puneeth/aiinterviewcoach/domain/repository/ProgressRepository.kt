package com.puneeth.aiinterviewcoach.domain.repository

import com.puneeth.aiinterviewcoach.domain.model.ProgressSummary
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun observeProgressSummary(): Flow<ProgressSummary>
    fun observeLastOpenedQuestionId(): Flow<Long?>
    suspend fun markQuestionViewed(questionId: Long)
    suspend fun markQuestionCompleted(questionId: Long)
    suspend fun saveLastOpenedQuestion(questionId: Long)
    suspend fun resetProgress()
}
