package com.puneeth.aiinterviewcoach.data.repository

import com.puneeth.aiinterviewcoach.data.local.dao.QuestionDao
import com.puneeth.aiinterviewcoach.data.local.mapper.toDomain
import com.puneeth.aiinterviewcoach.data.remote.GeminiRemoteDataSource
import com.puneeth.aiinterviewcoach.data.remote.dto.QuestionAssetDto
import com.puneeth.aiinterviewcoach.domain.model.CategorySummary
import com.puneeth.aiinterviewcoach.domain.model.HomeSummary
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.model.PracticeQuestion
import com.puneeth.aiinterviewcoach.domain.repository.QuestionRepository
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Singleton
class QuestionRepositoryImpl @Inject constructor(
    private val questionDao: QuestionDao,
    private val assetDataSource: GeminiRemoteDataSource,
    private val json: Json,
) : QuestionRepository {
    override fun observeHomeSummary(): Flow<HomeSummary> = combine(
        questionDao.observeTotalCount(),
        questionDao.observeBookmarksCount(),
        questionDao.observeCategorySummaries(""),
        questionDao.observeDifficultySummaries(""),
    ) { total, bookmarks, categories, difficulties ->
        HomeSummary(
            totalQuestions = total,
            bookmarksCount = bookmarks,
            continueQuestionId = null,
            categories = categories.map { it.toDomain() },
            difficultySummaries = difficulties.map { it.toDomain() },
        )
    }

    override fun observeCategories(searchQuery: String): Flow<List<CategorySummary>> {
        return questionDao.observeCategorySummaries(searchQuery).map { rows -> rows.map { it.toDomain() } }
    }

    override fun observeQuestions(
        searchQuery: String,
        category: InterviewCategory?,
        difficulty: InterviewDifficulty?,
        bookmarksOnly: Boolean,
    ): Flow<List<PracticeQuestion>> {
        return questionDao.observeQuestions(
            searchQuery = searchQuery,
            category = category?.title,
            difficulty = difficulty?.title,
            bookmarksOnly = bookmarksOnly,
        ).map { list -> list.map { it.toDomain() } }
    }

    override fun observeQuestion(questionId: Long): Flow<PracticeQuestion?> {
        return questionDao.observeQuestion(questionId).map { it?.toDomain() }
    }

    override suspend fun getQuestion(questionId: Long): PracticeQuestion? {
        return questionDao.getQuestion(questionId)?.toDomain()
    }

    override suspend fun getQuestionIds(
        category: InterviewCategory?,
        difficulty: InterviewDifficulty?,
        searchQuery: String,
        bookmarksOnly: Boolean,
    ): List<Long> {
        return questionDao.getQuestionIds(
            searchQuery = searchQuery,
            category = category?.title,
            difficulty = difficulty?.title,
            bookmarksOnly = bookmarksOnly,
        )
    }

    override suspend fun toggleBookmark(questionId: Long) {
        questionDao.toggleBookmark(questionId)
    }

    override suspend fun importQuestionPacksIfNeeded() {
        val assetQuestions = assetDataSource.loadQuestionPacks()
        if (questionDao.count() < assetQuestions.size) {
            val bookmarkedQuestionIds = questionDao.getBookmarkedQuestionIds().toSet()
            val questions = assetQuestions.map { question ->
                question.toEntity(isBookmarked = question.id in bookmarkedQuestionIds || question.isBookmarked)
            }
            questionDao.insertAll(questions)
        }
    }

    override suspend fun exportBookmarks(destinationDir: File): File {
        val questions = questionDao.getQuestionIds(bookmarksOnly = true, searchQuery = "", category = null, difficulty = null)
            .mapNotNull { questionDao.getQuestion(it) }
            .map { it.toDomain() }
            .map { question ->
                QuestionAssetDto(
                    id = question.id,
                    category = question.category.title,
                    difficulty = question.difficulty.title,
                    question = question.question,
                    answer = question.answer,
                    explanation = question.explanation,
                    tags = question.tags,
                    isBookmarked = question.isBookmarked,
                )
            }
        if (!destinationDir.exists()) {
            destinationDir.mkdirs()
        }
        val file = File(destinationDir, "bookmarked-questions.json")
        file.writeText(json.encodeToString(questions))
        return file
    }
}

private fun QuestionAssetDto.toEntity(
    isBookmarked: Boolean = this.isBookmarked,
) = com.puneeth.aiinterviewcoach.data.local.entity.QuestionEntity(
    id = id,
    category = category,
    difficulty = difficulty,
    question = question,
    answer = answer,
    explanation = explanation,
    tags = tags.joinToString(","),
    isBookmarked = isBookmarked,
)
