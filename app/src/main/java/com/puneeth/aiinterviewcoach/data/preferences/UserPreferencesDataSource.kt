package com.puneeth.aiinterviewcoach.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
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
            preferredCategory = InterviewCategory.fromTitle(
                prefs[Keys.PREFERRED_CATEGORY] ?: InterviewCategory.KOTLIN.title,
            ),
            preferredDifficulty = InterviewDifficulty.fromTitle(
                prefs[Keys.PREFERRED_DIFFICULTY] ?: InterviewDifficulty.INTERMEDIATE.title,
            ),
            darkMode = prefs[Keys.DARK_MODE] ?: true,
            dynamicColor = prefs[Keys.DYNAMIC_COLOR] ?: true,
            lastOpenedQuestionId = prefs[Keys.LAST_OPENED_QUESTION_ID],
            dailyStreak = prefs[Keys.DAILY_STREAK] ?: 0,
            lastPracticeEpochDay = prefs[Keys.LAST_PRACTICE_EPOCH_DAY] ?: 0L,
        )
    }

    suspend fun updatePreferredCategory(category: InterviewCategory) {
        dataStore.edit { it[Keys.PREFERRED_CATEGORY] = category.title }
    }

    suspend fun updatePreferredDifficulty(difficulty: InterviewDifficulty) {
        dataStore.edit { it[Keys.PREFERRED_DIFFICULTY] = difficulty.title }
    }

    suspend fun updateDarkMode(enabled: Boolean) {
        dataStore.edit { it[Keys.DARK_MODE] = enabled }
    }

    suspend fun updateDynamicColor(enabled: Boolean) {
        dataStore.edit { it[Keys.DYNAMIC_COLOR] = enabled }
    }

    suspend fun updateLastOpenedQuestion(questionId: Long?) {
        dataStore.edit {
            if (questionId == null) {
                it.remove(Keys.LAST_OPENED_QUESTION_ID)
            } else {
                it[Keys.LAST_OPENED_QUESTION_ID] = questionId
            }
        }
    }

    suspend fun updateDailyPractice(lastPracticeEpochDay: Long, streak: Int) {
        dataStore.edit {
            it[Keys.LAST_PRACTICE_EPOCH_DAY] = lastPracticeEpochDay
            it[Keys.DAILY_STREAK] = streak
        }
    }

    private object Keys {
        val PREFERRED_CATEGORY = stringPreferencesKey("preferred_category")
        val PREFERRED_DIFFICULTY = stringPreferencesKey("preferred_difficulty")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val LAST_OPENED_QUESTION_ID = longPreferencesKey("last_opened_question_id")
        val LAST_PRACTICE_EPOCH_DAY = longPreferencesKey("last_practice_epoch_day")
        val DAILY_STREAK = intPreferencesKey("daily_streak")
    }
}
