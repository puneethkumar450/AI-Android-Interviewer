package com.puneeth.aiinterviewcoach.domain.repository

import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import kotlinx.coroutines.flow.Flow

data class UserPreferences(
    val preferredCategory: InterviewCategory = InterviewCategory.KOTLIN,
    val preferredDifficulty: InterviewDifficulty = InterviewDifficulty.MID,
    val darkMode: Boolean = true,
)

interface UserPreferencesRepository {
    fun observePreferences(): Flow<UserPreferences>
    suspend fun updatePreferredCategory(category: InterviewCategory)
    suspend fun updatePreferredDifficulty(difficulty: InterviewDifficulty)
    suspend fun updateDarkMode(enabled: Boolean)
}
