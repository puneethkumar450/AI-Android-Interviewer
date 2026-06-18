package com.puneeth.aiinterviewcoach.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneeth.aiinterviewcoach.domain.repository.UserPreferences
import com.puneeth.aiinterviewcoach.domain.repository.UserPreferencesRepository
import com.puneeth.aiinterviewcoach.domain.usecase.ImportQuestionPacksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RootUiState(
    val preferences: UserPreferences = UserPreferences(),
    val isReady: Boolean = false,
)

@HiltViewModel
class RootViewModel @Inject constructor(
    preferencesRepository: UserPreferencesRepository,
    private val importQuestionPacks: ImportQuestionPacksUseCase,
) : ViewModel() {
    private val isReady = MutableStateFlow(false)

    val uiState: StateFlow<RootUiState> = combine(
        preferencesRepository.observePreferences(),
        isReady,
    ) { preferences, ready ->
        RootUiState(preferences = preferences, isReady = ready)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RootUiState(),
    )

    init {
        viewModelScope.launch {
            importQuestionPacks()
            isReady.value = true
        }
    }
}
