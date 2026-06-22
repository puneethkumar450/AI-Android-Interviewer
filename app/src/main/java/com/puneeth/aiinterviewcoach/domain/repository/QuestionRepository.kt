package com.puneeth.aiinterviewcoach.domain.repository

import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.model.CategorySummary
import com.puneeth.aiinterviewcoach.domain.model.HomeSummary
import com.puneeth.aiinterviewcoach.domain.model.PracticeQuestion
import kotlinx.coroutines.flow.Flow
import java.io.File

interface QuestionRepository {
    fun observeHomeSummary(): Flow<HomeSummary>

    fun observeCategories(searchQuery: String = ""): Flow<List<CategorySummary>>

    fun observeQuestions(
        searchQuery: String = "",
        category: InterviewCategory? = null,
        difficulty: InterviewDifficulty? = null,
        bookmarksOnly: Boolean = false,
    ): Flow<List<PracticeQuestion>>

    fun observeQuestion(questionId: Long): Flow<PracticeQuestion?>
    suspend fun getQuestion(questionId: Long): PracticeQuestion?
    suspend fun getQuestionIds(
        category: InterviewCategory? = null,
        difficulty: InterviewDifficulty? = null,
        searchQuery: String = "",
        bookmarksOnly: Boolean = false,
        hardOnly: Boolean = false,
    ): List<Long>

    suspend fun toggleBookmark(questionId: Long)
    suspend fun importQuestionPacksIfNeeded()
    suspend fun exportBookmarks(destinationDir: File): File
}
