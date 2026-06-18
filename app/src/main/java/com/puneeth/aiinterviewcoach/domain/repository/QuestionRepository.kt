package com.puneeth.aiinterviewcoach.domain.repository

import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.model.PracticeQuestion
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    fun observeQuestions(
        searchQuery: String = "",
        category: InterviewCategory? = null,
        difficulty: InterviewDifficulty? = null,
        bookmarksOnly: Boolean = false,
    ): Flow<List<PracticeQuestion>>

    suspend fun toggleBookmark(questionId: Long)
    suspend fun seedIfNeeded()
}
