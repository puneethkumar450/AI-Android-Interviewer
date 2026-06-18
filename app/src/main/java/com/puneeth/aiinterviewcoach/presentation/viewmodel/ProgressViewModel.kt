package com.puneeth.aiinterviewcoach.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneeth.aiinterviewcoach.domain.model.ProgressSummary
import com.puneeth.aiinterviewcoach.domain.model.ResultAnalysis
import com.puneeth.aiinterviewcoach.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ProgressUiState(
    val summary: ProgressSummary = ProgressSummary(
        completedInterviews = 0,
        currentStreak = 0,
        averageScore = 0,
        categoryScores = emptyMap(),
        weakAreas = emptyList(),
    ),
    val analysis: ResultAnalysis = ResultAnalysis(
        strengths = emptyList(),
        weaknesses = emptyList(),
        improvementSuggestions = emptyList(),
        recommendedLearningTopics = emptyList(),
    ),
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    progressRepository: ProgressRepository,
) : ViewModel() {
    val uiState: StateFlow<ProgressUiState> = combine(
        progressRepository.observeProgressSummary(),
        progressRepository.observeLatestAnalysis(),
    ) { summary, analysis ->
        ProgressUiState(summary = summary, analysis = analysis)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProgressUiState(),
    )
}
