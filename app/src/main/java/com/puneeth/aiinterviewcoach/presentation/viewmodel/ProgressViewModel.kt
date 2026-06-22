package com.puneeth.aiinterviewcoach.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneeth.aiinterviewcoach.domain.model.CategoryConfidenceSummary
import com.puneeth.aiinterviewcoach.domain.model.ProgressSummary
import com.puneeth.aiinterviewcoach.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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
    val categoryConfidence: List<CategoryConfidenceSummary> = emptyList(),
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    progressRepository: ProgressRepository,
) : ViewModel() {
    val uiState: StateFlow<ProgressUiState> = combine(
        progressRepository.observeProgressSummary(),
        progressRepository.observeCategoryConfidence(),
    ) { summary, confidence ->
        ProgressUiState(summary = summary, categoryConfidence = confidence)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProgressUiState())
}
