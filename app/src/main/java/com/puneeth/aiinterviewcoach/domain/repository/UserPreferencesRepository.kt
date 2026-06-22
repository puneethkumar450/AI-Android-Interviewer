package com.puneeth.aiinterviewcoach.domain.repository

import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import kotlinx.coroutines.flow.Flow

data class UserPreferences(
    val preferredCategory: InterviewCategory = InterviewCategory.KOTLIN,
    val preferredDifficulty: InterviewDifficulty = InterviewDifficulty.INTERMEDIATE,
    val darkMode: Boolean = true,
    val dynamicColor: Boolean = true,
    val speechRate: Float = 1f,
    val speechPitch: Float = 1f,
    val speechAutoRead: Boolean = false,
    val speechHighlight: Boolean = true,
    val speechPreferOffline: Boolean = true,
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
    suspend fun updateSpeechRate(rate: Float)
    suspend fun updateSpeechPitch(pitch: Float)
    suspend fun updateSpeechAutoRead(enabled: Boolean)
    suspend fun updateSpeechHighlight(enabled: Boolean)
    suspend fun updateSpeechPreferOffline(enabled: Boolean)
    suspend fun updateLastOpenedQuestion(questionId: Long?)
    suspend fun updateDailyPractice(lastPracticeEpochDay: Long, streak: Int)
}
