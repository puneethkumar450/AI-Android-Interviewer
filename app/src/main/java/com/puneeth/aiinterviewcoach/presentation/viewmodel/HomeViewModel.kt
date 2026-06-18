package com.puneeth.aiinterviewcoach.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneeth.aiinterviewcoach.domain.model.CategorySummary
import com.puneeth.aiinterviewcoach.domain.model.HomeSummary
import com.puneeth.aiinterviewcoach.domain.model.HomeDifficultySummary
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.usecase.ObserveHomeSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val summary: HomeSummary = HomeSummary(
        totalQuestions = 0,
        bookmarksCount = 0,
        continueQuestionId = null,
        categories = InterviewCategory.entries.map { category -> CategorySummary(category, 0) },
        difficultySummaries = InterviewDifficulty.entries.map { difficulty -> HomeDifficultySummary(difficulty, 0) },
    ),
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeHomeSummary: ObserveHomeSummaryUseCase,
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = observeHomeSummary()
        .map { HomeUiState(summary = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
}
