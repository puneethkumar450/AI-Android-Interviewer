package com.puneeth.aiinterviewcoach.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneeth.aiinterviewcoach.domain.model.ProgressSummary
import com.puneeth.aiinterviewcoach.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

data class ProgressUiState(
    val summary: ProgressSummary = ProgressSummary(
        totalQuestions = 0,
        viewedQuestions = 0,
        completedQuestions = 0,
        currentStreak = 0,
        bookmarksCount = 0,
        continueQuestionId = null,
        categoryProgress = emptyList(),
        difficultyProgress = emptyList(),
    ),
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    progressRepository: ProgressRepository,
) : ViewModel() {
    val uiState: StateFlow<ProgressUiState> = progressRepository.observeProgressSummary()
        .map { ProgressUiState(summary = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProgressUiState())
}
