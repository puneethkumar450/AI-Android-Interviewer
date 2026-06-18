package com.puneeth.aiinterviewcoach.domain.repository

import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import kotlinx.coroutines.flow.Flow

data class UserPreferences(
    val preferredCategory: InterviewCategory = InterviewCategory.KOTLIN,
    val preferredDifficulty: InterviewDifficulty = InterviewDifficulty.INTERMEDIATE,
    val darkMode: Boolean = true,
    val dynamicColor: Boolean = true,
    val lastOpenedQuestionId: Long? = null,
    val dailyStreak: Int = 0,
    val lastPracticeEpochDay: Long = 0L,
)

interface UserPreferencesRepository {
    fun observePreferences(): Flow<UserPreferences>
    suspend fun updatePreferredCategory(category: InterviewCategory)
    suspend fun updatePreferredDifficulty(difficulty: InterviewDifficulty)
    suspend fun updateDarkMode(enabled: Boolean)
    suspend fun updateDynamicColor(enabled: Boolean)
    suspend fun updateLastOpenedQuestion(questionId: Long?)
    suspend fun updateDailyPractice(lastPracticeEpochDay: Long, streak: Int)
}
