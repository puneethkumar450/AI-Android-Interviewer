package com.puneeth.aiinterviewcoach.data.repository

import com.puneeth.aiinterviewcoach.data.local.dao.QuestionDao
import com.puneeth.aiinterviewcoach.data.local.dao.QuestionProgressDao
import com.puneeth.aiinterviewcoach.data.local.entity.QuestionProgressEntity
import com.puneeth.aiinterviewcoach.data.local.mapper.toCategoryProgress
import com.puneeth.aiinterviewcoach.data.local.mapper.toDifficultyProgress
import com.puneeth.aiinterviewcoach.domain.model.ProgressSummary
import com.puneeth.aiinterviewcoach.domain.model.RecentActivity
import com.puneeth.aiinterviewcoach.domain.repository.ProgressRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import com.puneeth.aiinterviewcoach.domain.repository.UserPreferencesRepository

@Singleton
class ProgressRepositoryImpl @Inject constructor(
    private val questionDao: QuestionDao,
    private val progressDao: QuestionProgressDao,
    private val preferencesRepository: UserPreferencesRepository,
) : ProgressRepository {
    override fun observeProgressSummary(): Flow<ProgressSummary> = combine(
        questionDao.observeTotalCount(),
        progressDao.observeViewedCount(),
        progressDao.observeCompletedCount(),
        questionDao.observeBookmarksCount(),
    ) { total, viewed, completed, bookmarks ->
        BasicProgressCounts(total, viewed, completed, bookmarks)
    }.combine(
        progressDao.observeCategoryProgress(),
    ) { basic, categoryRows ->
        basic to categoryRows
    }.combine(
        progressDao.observeDifficultyProgress(),
    ) { (basic, categoryRows), difficultyRows ->
        Triple(basic, categoryRows, difficultyRows)
    }.combine(
        preferencesRepository.observePreferences(),
    ) { (basic, categoryRows, difficultyRows), preferences ->
        ProgressSummary(
            totalQuestions = basic.totalQuestions,
            viewedQuestions = basic.viewedQuestions,
            completedQuestions = basic.completedQuestions,
            currentStreak = preferences.dailyStreak,
            bookmarksCount = basic.bookmarksCount,
            continueQuestionId = preferences.lastOpenedQuestionId,
            categoryProgress = categoryRows.map { it.toCategoryProgress() },
            difficultyProgress = difficultyRows.map { it.toDifficultyProgress() },
        )
    }

    override fun observeLastOpenedQuestionId(): Flow<Long?> {
        return preferencesRepository.observePreferences().map { it.lastOpenedQuestionId }
    }

    override fun observeRecentActivity(): Flow<RecentActivity?> {
        return progressDao.observeLastViewed().map { row ->
            row?.let { RecentActivity(categoryTitle = it.category, lastViewedAt = it.lastViewedAt) }
        }
    }

    override fun observeUnviewedCount(): Flow<Int> {
        return progressDao.observeUnviewedCount()
    }

    override suspend fun markQuestionViewed(questionId: Long) {
        upsertProgress(questionId, completed = false)
        updateDailyPractice()
    }

    override suspend fun markQuestionCompleted(questionId: Long) {
        upsertProgress(questionId, completed = true)
        updateDailyPractice()
    }

    override suspend fun saveLastOpenedQuestion(questionId: Long) {
        preferencesRepository.updateLastOpenedQuestion(questionId)
    }

    override suspend fun resetProgress() {
        progressDao.clearAll()
        preferencesRepository.updateLastOpenedQuestion(null)
        preferencesRepository.updateDailyPractice(0L, 0)
    }

    private suspend fun upsertProgress(questionId: Long, completed: Boolean) {
        val now = System.currentTimeMillis()
        val current = progressDao.getProgress(questionId)
        val updated = QuestionProgressEntity(
            questionId = questionId,
            viewedCount = (current?.viewedCount ?: 0) + 1,
            completedCount = (current?.completedCount ?: 0) + if (completed) 1 else 0,
            firstViewedAt = current?.firstViewedAt ?: now,
            lastViewedAt = now,
            lastCompletedAt = if (completed) now else current?.lastCompletedAt,
        )
        progressDao.upsert(updated)
    }

    private suspend fun updateDailyPractice() {
        val today = LocalDate.now().toEpochDay()
        val prefs = preferencesRepository.observePreferences().first()
        val newStreak = when {
            prefs.lastPracticeEpochDay == today -> prefs.dailyStreak
            prefs.lastPracticeEpochDay == today - 1 -> prefs.dailyStreak + 1
            else -> 1
        }
        preferencesRepository.updateDailyPractice(today, newStreak)
    }

    private data class BasicProgressCounts(
        val totalQuestions: Int,
        val viewedQuestions: Int,
        val completedQuestions: Int,
        val bookmarksCount: Int,
    )
}
