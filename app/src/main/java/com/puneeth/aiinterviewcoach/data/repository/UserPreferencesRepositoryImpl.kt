package com.puneeth.aiinterviewcoach.data.repository

import com.puneeth.aiinterviewcoach.data.preferences.UserPreferencesDataSource
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.repository.UserPreferences
import com.puneeth.aiinterviewcoach.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataSource: UserPreferencesDataSource,
) : UserPreferencesRepository {
    override fun observePreferences(): Flow<UserPreferences> = dataSource.preferences

    override suspend fun updatePreferredCategory(category: InterviewCategory) {
        dataSource.updatePreferredCategory(category)
    }

    override suspend fun updatePreferredDifficulty(difficulty: InterviewDifficulty) {
        dataSource.updatePreferredDifficulty(difficulty)
    }

    override suspend fun updateDarkMode(enabled: Boolean) {
        dataSource.updateDarkMode(enabled)
    }
}
