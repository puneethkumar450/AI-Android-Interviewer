package com.puneeth.aiinterviewcoach.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
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
            speechRate = prefs[Keys.SPEECH_RATE] ?: 1f,
            speechPitch = prefs[Keys.SPEECH_PITCH] ?: 1f,
            speechAutoRead = prefs[Keys.SPEECH_AUTO_READ] ?: false,
            speechHighlight = prefs[Keys.SPEECH_HIGHLIGHT] ?: true,
            speechPreferOffline = prefs[Keys.SPEECH_PREFER_OFFLINE] ?: true,
            lastOpenedQuestionId = prefs[Keys.LAST_OPENED_QUESTION_ID],
            dailyStreak = prefs[Keys.DAILY_STREAK] ?: 0,
            lastPracticeEpochDay = prefs[Keys.LAST_PRACTICE_EPOCH_DAY] ?: 0L,
            dailyGoal = prefs[Keys.DAILY_GOAL] ?: 5,
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

    suspend fun updateSpeechRate(rate: Float) { dataStore.edit { it[Keys.SPEECH_RATE] = rate } }
    suspend fun updateSpeechPitch(pitch: Float) { dataStore.edit { it[Keys.SPEECH_PITCH] = pitch } }
    suspend fun updateSpeechAutoRead(enabled: Boolean) { dataStore.edit { it[Keys.SPEECH_AUTO_READ] = enabled } }
    suspend fun updateSpeechHighlight(enabled: Boolean) { dataStore.edit { it[Keys.SPEECH_HIGHLIGHT] = enabled } }
    suspend fun updateSpeechPreferOffline(enabled: Boolean) { dataStore.edit { it[Keys.SPEECH_PREFER_OFFLINE] = enabled } }

    suspend fun updateLastOpenedQuestion(questionId: Long?) {
        dataStore.edit {
            if (questionId == null) {
                it.remove(Keys.LAST_OPENED_QUESTION_ID)
            } else {
                it[Keys.LAST_OPENED_QUESTION_ID] = questionId
            }
        }
    }

    suspend fun updateDailyGoal(goal: Int) {
        dataStore.edit { it[Keys.DAILY_GOAL] = goal.coerceIn(1, 50) }
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
        val SPEECH_RATE = floatPreferencesKey("speech_rate")
        val SPEECH_PITCH = floatPreferencesKey("speech_pitch")
        val SPEECH_AUTO_READ = booleanPreferencesKey("speech_auto_read")
        val SPEECH_HIGHLIGHT = booleanPreferencesKey("speech_highlight")
        val SPEECH_PREFER_OFFLINE = booleanPreferencesKey("speech_prefer_offline")
        val LAST_OPENED_QUESTION_ID = longPreferencesKey("last_opened_question_id")
        val LAST_PRACTICE_EPOCH_DAY = longPreferencesKey("last_practice_epoch_day")
        val DAILY_STREAK = intPreferencesKey("daily_streak")
        val DAILY_GOAL = intPreferencesKey("daily_goal")
    }
}
