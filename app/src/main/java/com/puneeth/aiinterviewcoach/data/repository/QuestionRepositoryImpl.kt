package com.puneeth.aiinterviewcoach.data.repository

import com.puneeth.aiinterviewcoach.data.local.dao.QuestionDao
import com.puneeth.aiinterviewcoach.data.local.mapper.toDomain
import com.puneeth.aiinterviewcoach.data.local.seed.QuestionSeedData
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.model.PracticeQuestion
import com.puneeth.aiinterviewcoach.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val questionDao: QuestionDao,
) : QuestionRepository {
    override fun observeQuestions(
        searchQuery: String,
        category: InterviewCategory?,
        difficulty: InterviewDifficulty?,
        bookmarksOnly: Boolean,
    ): Flow<List<PracticeQuestion>> {
        return questionDao.observeQuestions(
            searchQuery = searchQuery,
            category = category?.name,
            difficulty = difficulty?.name,
            bookmarksOnly = bookmarksOnly,
        ).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun toggleBookmark(questionId: Long) {
        questionDao.toggleBookmark(questionId)
    }

    override suspend fun seedIfNeeded() {
        if (questionDao.count() == 0) {
            questionDao.insertAll(QuestionSeedData.questions)
        }
    }
}
