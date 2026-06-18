package com.puneeth.aiinterviewcoach.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.repository.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val preferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            preferredCategory = InterviewCategory.valueOf(
                prefs[Keys.PREFERRED_CATEGORY] ?: InterviewCategory.KOTLIN.name,
            ),
            preferredDifficulty = InterviewDifficulty.valueOf(
                prefs[Keys.PREFERRED_DIFFICULTY] ?: InterviewDifficulty.MID.name,
            ),
            darkMode = prefs[Keys.DARK_MODE] ?: true,
        )
    }

    suspend fun updatePreferredCategory(category: InterviewCategory) {
        dataStore.edit { it[Keys.PREFERRED_CATEGORY] = category.name }
    }

    suspend fun updatePreferredDifficulty(difficulty: InterviewDifficulty) {
        dataStore.edit { it[Keys.PREFERRED_DIFFICULTY] = difficulty.name }
    }

    suspend fun updateDarkMode(enabled: Boolean) {
        dataStore.edit { it[Keys.DARK_MODE] = enabled }
    }

    private object Keys {
        val PREFERRED_CATEGORY = stringPreferencesKey("preferred_category")
        val PREFERRED_DIFFICULTY = stringPreferencesKey("preferred_difficulty")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }
}
