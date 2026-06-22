package com.puneeth.aiinterviewcoach.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneeth.aiinterviewcoach.domain.repository.UserPreferences
import com.puneeth.aiinterviewcoach.domain.repository.UserPreferencesRepository
import com.puneeth.aiinterviewcoach.domain.usecase.ExportBookmarksUseCase
import com.puneeth.aiinterviewcoach.domain.usecase.ResetProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val preferences: UserPreferences = UserPreferences(),
    val exportedFilePath: String? = null,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    private val resetProgress: ResetProgressUseCase,
    private val exportBookmarks: ExportBookmarksUseCase,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val exportedPath = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)

    val uiState: StateFlow<SettingsUiState> = preferencesRepository.observePreferences()
        .map { SettingsUiState(preferences = it, exportedFilePath = exportedPath.value) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun updateDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateDarkMode(enabled)
        }
    }

    fun updateDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateDynamicColor(enabled)
        }
    }

    fun updateSpeechRate(rate: Float) = viewModelScope.launch { preferencesRepository.updateSpeechRate(rate) }
    fun updateSpeechPitch(pitch: Float) = viewModelScope.launch { preferencesRepository.updateSpeechPitch(pitch) }
    fun updateSpeechAutoRead(enabled: Boolean) = viewModelScope.launch { preferencesRepository.updateSpeechAutoRead(enabled) }
    fun updateSpeechHighlight(enabled: Boolean) = viewModelScope.launch { preferencesRepository.updateSpeechHighlight(enabled) }
    fun updateSpeechPreferOffline(enabled: Boolean) = viewModelScope.launch { preferencesRepository.updateSpeechPreferOffline(enabled) }

    fun resetProgress() {
        viewModelScope.launch {
            resetProgress.invoke()
        }
    }

    fun exportBookmarks() {
        viewModelScope.launch {
            val file: File = exportBookmarks(context.cacheDir)
            exportedPath.value = file.absolutePath
        }
    }
}
