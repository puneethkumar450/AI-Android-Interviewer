package com.puneeth.aiinterviewcoach.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneeth.aiinterviewcoach.domain.model.HomeDifficultySummary
import com.puneeth.aiinterviewcoach.domain.model.CategorySummary
import com.puneeth.aiinterviewcoach.domain.model.PracticeQuestion
import com.puneeth.aiinterviewcoach.domain.usecase.ObserveHomeSummaryUseCase
import com.puneeth.aiinterviewcoach.domain.usecase.ObserveQuestionBankUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class SearchUiState(
    val query: String = "",
    val questions: List<PracticeQuestion> = emptyList(),
    val categories: List<CategorySummary> = emptyList(),
    val difficultySummaries: List<HomeDifficultySummary> = emptyList(),
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val observeQuestions: ObserveQuestionBankUseCase,
    observeHomeSummary: ObserveHomeSummaryUseCase,
) : ViewModel() {
    private val query = MutableStateFlow("")

    private val searchResults = query.flatMapLatest { search ->
        observeQuestions(
            searchQuery = search,
            category = null,
            difficulty = null,
            bookmarksOnly = false,
        ).map { search to it }
    }

    val uiState: StateFlow<SearchUiState> = combine(
        searchResults,
        observeHomeSummary(),
    ) { (query, questions), homeSummary ->
        SearchUiState(
            query = query,
            questions = questions,
            categories = homeSummary.categories,
            difficultySummaries = homeSummary.difficultySummaries,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchUiState())

    fun updateQuery(value: String) {
        query.value = value
    }
}
